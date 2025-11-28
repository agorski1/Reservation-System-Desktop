module com.reservio.reservation_system.reservationsystemdesktop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.reservio.reservation_system.reservationsystemdesktop to javafx.fxml;
    exports com.reservio.reservation_system.reservationsystemdesktop;
}