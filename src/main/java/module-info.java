module org.example.socket_serversocket {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens org.example.socket_serversocket to javafx.fxml;
    exports org.example.socket_serversocket;
}