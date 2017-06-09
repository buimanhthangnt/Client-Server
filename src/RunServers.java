class RunServers {
    public static void main(String[] args) {
        int portFrontend = Integer.parseInt(args[0]);
        int portMD5 = Integer.parseInt(args[1]);
        int portSHA = Integer.parseInt(args[2]);
        Thread frontendServerThread = new Thread(() -> FrontendServer.run(portFrontend, portMD5, portSHA));
        Thread shaServerThread = new Thread(() -> ServerSHA256.run(portSHA));
        Thread md5ServerThread = new Thread(() -> ServerMD5.run(portMD5));
        frontendServerThread.start();
        shaServerThread.start();
        md5ServerThread.start();
    }
}
