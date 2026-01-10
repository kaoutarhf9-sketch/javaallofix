package presentation;

import dao.*;
import metier.*;
import javax.swing.*;
import java.awt.*;

public class ReparationPanel extends JPanel {

    private JPanel devicesContainer;

    public ReparationPanel() {
        setLayout(new BorderLayout());

        JButton btnAdd = new JButton("➕ Ajouter device");
        btnAdd.addActionListener(e -> addDeviceForm());

        devicesContainer = new JPanel();
        devicesContainer.setLayout(new BoxLayout(devicesContainer, BoxLayout.Y_AXIS));

        add(btnAdd, BorderLayout.NORTH);
        add(new JScrollPane(devicesContainer), BorderLayout.CENTER);

        addDeviceForm();
    }

    private void addDeviceForm() {
        devicesContainer.add(new DeviceFormPanel());
        revalidate();
        repaint();
    }
}