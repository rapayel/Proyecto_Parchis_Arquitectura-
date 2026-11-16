package org.entrada;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import org.comunes.ContextoConexion;
import org.comunes.Observer;
import org.comunes.Subject;

public class ServidorTCP implements Subject, Runnable {
    private static final int MAX_CLIENTES_PERMITIDOS = 4;
    private static final int MAX_BYTES_POR_MENSAJE = 1_048_576;

    private final int puerto;
    private final int maxClientes;
    private final BiFunction<Integer, Boolean, byte[]> crearAsignacion;
    private final boolean[] idsAsignados;
    private final CopyOnWriteArrayList<Observer> observers = new CopyOnWriteArrayList<>();
    private final List<ClienteConectado> clientes = new CopyOnWriteArrayList<>();
    private volatile boolean activo;
    private volatile boolean partidaIniciada;
    private volatile ServerSocket serverSocket;
    private ContextoConexion contextoActual;
    private ExecutorService hilosClientes;

    public ServidorTCP(int puerto) {
        this(puerto, MAX_CLIENTES_PERMITIDOS, null);
    }

    public ServidorTCP(
            int puerto,
            int maxClientes,
            BiFunction<Integer, Boolean, byte[]> crearAsignacion) {
        if (puerto < 0 || puerto > 65535) {
            throw new IllegalArgumentException("El puerto debe estar entre 0 y 65535.");
        }
        if (maxClientes < 2 || maxClientes > MAX_CLIENTES_PERMITIDOS) {
            throw new IllegalArgumentException("La sala debe tener entre 2 y 4 jugadores.");
        }
        this.puerto = puerto;
        this.maxClientes = maxClientes;
        this.crearAsignacion = crearAsignacion;
        this.idsAsignados = new boolean[maxClientes];
    }

    public synchronized void iniciar() throws IOException {
        if (activo) {
            return;
        }

        ServerSocket servidor = new ServerSocket(puerto);
        serverSocket = servidor;
        hilosClientes = Executors.newFixedThreadPool(maxClientes);
        activo = true;
        new Thread(this, "ServidorTCP-Hilo").start();
    }

    public void detener() {
        activo = false;
        ServerSocket servidor = serverSocket;
        if (servidor != null) {
            try {
                servidor.close();
            } catch (IOException e) {
                System.err.println("[ServidorTCP] Error al cerrar el servidor: " + e.getMessage());
            }
        }

        for (ClienteConectado cliente : clientes) {
            cerrarCliente(cliente);
        }

        ExecutorService executor = hilosClientes;
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public int getPuertoEscucha() {
        ServerSocket servidor = serverSocket;
        return servidor == null ? puerto : servidor.getLocalPort();
    }

    public int getNumeroClientesConectados() {
        return clientes.size();
    }

    public void enviarATodos(byte[] bytes) {
        validarMensaje(bytes);
        for (ClienteConectado cliente : clientes) {
            try {
                cliente.enviar(bytes);
            } catch (IOException e) {
                System.err.println("[ServidorTCP] Se desconectó un cliente durante la difusión: "
                        + e.getMessage());
                cerrarCliente(cliente);
            }
        }
    }

    public void enviarAJugador(int idJugador, byte[] bytes) throws IOException {
        validarMensaje(bytes);
        for (ClienteConectado cliente : clientes) {
            if (cliente.idJugador + 1 == idJugador) {
                try {
                    cliente.enviar(bytes);
                    return;
                } catch (IOException e) {
                    cerrarCliente(cliente);
                    throw e;
                }
            }
        }
        throw new IOException("El jugador " + idJugador + " no tiene una conexión activa.");
    }

    @Override
    public void run() {
        System.out.println("[ServidorTCP] Escuchando en el puerto " + getPuertoEscucha() + ".");
        try {
            while (activo) {
                Socket socket = serverSocket.accept();
                aceptarCliente(socket);
            }
        } catch (IOException e) {
            if (activo) {
                System.err.println("[ServidorTCP] Error aceptando conexiones: " + e.getMessage());
            }
        } finally {
            detener();
            System.out.println("[ServidorTCP] Servidor detenido.");
        }
    }

    private void aceptarCliente(Socket socket) {
        int idJugador = asignarIdJugador();
        if (idJugador == -1) {
            System.err.println("[ServidorTCP] Se rechazó una conexión: la sala está completa.");
            cerrarSocket(socket);
            return;
        }

        ClienteConectado cliente = null;
        try {
            cliente = new ClienteConectado(socket, idJugador);
            clientes.add(cliente);
            enviarAsignacion(cliente, partidaIniciada);
            if (!partidaIniciada && clientes.size() == maxClientes) {
                partidaIniciada = true;
                difundirAsignaciones(true);
            }
            ClienteConectado clienteAceptado = cliente;
            hilosClientes.execute(() -> atenderCliente(clienteAceptado));
            System.out.println("[ServidorTCP] Jugador " + (idJugador + 1)
                    + " conectado. Total: " + clientes.size());
        } catch (IOException | RuntimeException e) {
            if (cliente != null) {
                clientes.remove(cliente);
            }
            liberarIdJugador(idJugador);
            cerrarSocket(socket);
            System.err.println("[ServidorTCP] No se pudo aceptar al cliente: " + e.getMessage());
        }
    }

    private void atenderCliente(ClienteConectado cliente) {
        try (DataInputStream entrada = new DataInputStream(cliente.socket.getInputStream())) {
            while (activo && !cliente.socket.isClosed()) {
                int longitud;
                try {
                    longitud = entrada.readInt();
                } catch (EOFException e) {
                    break;
                }

                if (longitud < 1 || longitud > MAX_BYTES_POR_MENSAJE) {
                    throw new IOException("Longitud de mensaje inválida: " + longitud);
                }

                byte[] bytes = new byte[longitud];
                entrada.readFully(bytes);
                if (!partidaIniciada) {
                    System.err.println("[ServidorTCP] Se ignoró una solicitud antes de iniciar la partida.");
                    continue;
                }
                notificarObservadores(new ContextoConexion(null, 0, bytes, cliente.idJugador + 1));
            }
        } catch (IOException e) {
            if (activo && !cliente.socket.isClosed()) {
                System.err.println("[ServidorTCP] Error recibiendo un mensaje: " + e.getMessage());
            }
        } finally {
            cerrarCliente(cliente);
        }
    }

    private synchronized void notificarObservadores(ContextoConexion contexto) {
        contextoActual = contexto;
        try {
            notifyObservers();
        } finally {
            contextoActual = null;
        }
    }

    @Override
    public void addObserver(Observer observer) {
        if (observer != null) {
            observers.addIfAbsent(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        ContextoConexion contexto = contextoActual;
        if (contexto == null) {
            return;
        }
        for (Observer observer : observers) {
            try {
                observer.update(contexto);
            } catch (RuntimeException e) {
                System.err.println("[ServidorTCP] No se pudo procesar el mensaje: " + e.getMessage());
            }
        }
    }

    private void cerrarCliente(ClienteConectado cliente) {
        if (clientes.remove(cliente)) {
            liberarIdJugador(cliente.idJugador);
            cerrarSocket(cliente.socket);
            System.out.println("[ServidorTCP] Jugador " + (cliente.idJugador + 1)
                    + " desconectado. Total: " + clientes.size());
        }
    }

    private synchronized int asignarIdJugador() {
        for (int i = 0; i < idsAsignados.length; i++) {
            if (!idsAsignados[i]) {
                idsAsignados[i] = true;
                return i;
            }
        }
        return -1;
    }

    private synchronized void liberarIdJugador(int idJugador) {
        idsAsignados[idJugador] = false;
    }

    private void enviarAsignacion(ClienteConectado cliente, boolean partidaLista) throws IOException {
        if (crearAsignacion == null) {
            return;
        }
        byte[] bytes = crearAsignacion.apply(cliente.idJugador + 1, partidaLista);
        validarMensaje(bytes);
        cliente.enviar(bytes);
    }

    private void difundirAsignaciones(boolean partidaLista) {
        for (ClienteConectado cliente : clientes) {
            try {
                enviarAsignacion(cliente, partidaLista);
            } catch (IOException e) {
                System.err.println("[ServidorTCP] No se pudo notificar el estado de la sala: "
                        + e.getMessage());
                cerrarCliente(cliente);
            }
        }
    }

    private static void validarMensaje(byte[] bytes) {
        if (bytes == null || bytes.length == 0 || bytes.length > MAX_BYTES_POR_MENSAJE) {
            throw new IllegalArgumentException("El mensaje debe contener entre 1 y "
                    + MAX_BYTES_POR_MENSAJE + " bytes.");
        }
    }

    private void cerrarSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("[ServidorTCP] Error al cerrar un socket: " + e.getMessage());
        }
    }

    private static final class ClienteConectado {
        private final Socket socket;
        private final DataOutputStream salida;
        private final int idJugador;

        private ClienteConectado(Socket socket, int idJugador) throws IOException {
            this.socket = socket;
            this.salida = new DataOutputStream(socket.getOutputStream());
            this.idJugador = idJugador;
        }

        private synchronized void enviar(byte[] bytes) throws IOException {
            salida.writeInt(bytes.length);
            salida.write(bytes);
            salida.flush();
        }
    }
}
