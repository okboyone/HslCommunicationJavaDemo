package cn.hslcommunication.HslCommunicationDemo.PLC.Fuji;

import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Profinet.Fuji.FujiSPHNet;
import cn.hslcommunication.HslCommunicationDemo.DemoUtils;
import cn.hslcommunication.HslCommunicationDemo.UserControlReadWriteDevice;
import cn.hslcommunication.HslCommunicationDemo.UserControlReadWriteHead;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FormFujiSPHNet extends JPanel {

    public FormFujiSPHNet(JTabbedPane tabbedPane){
        setLayout(null);
        add( new UserControlReadWriteHead("SPH", tabbedPane, this));
        AddConnectSegment(this);
        plc = new FujiSPHNet();

        userControlReadWriteDevice = DemoUtils.CreateDevicePanel(this);
        userControlReadWriteDevice.setEnabled(false);
    }

    private FujiSPHNet plc = null;
    private String defaultAddress = "M1.100";
    private UserControlReadWriteDevice userControlReadWriteDevice = null;


    public void AddConnectSegment(JPanel panel){
        JPanel panelConnect = DemoUtils.CreateConnectPanel(panel);

        JLabel label1 = new JLabel("Ip：");
        label1.setBounds(8, 17,56, 17);
        panelConnect.add(label1);

        JTextField textField1 = new JTextField();
        textField1.setBounds(62,14,106, 23);
        textField1.setText("127.0.0.1");
        panelConnect.add(textField1);

        JLabel label2 = new JLabel("Port：");
        label2.setBounds(184, 17,56, 17);
        panelConnect.add(label2);

        JTextField textField2 = new JTextField();
        textField2.setBounds(238,14,61, 23);
        textField2.setText("507");
        panelConnect.add(textField2);

        JLabel label3 = new JLabel("ConnectId：");
        label3.setBounds(338, 17,56, 17);
        panelConnect.add(label3);

        JTextField textField3 = new JTextField();
        textField3.setBounds(412,14,40, 23);
        textField3.setText("254");
        panelConnect.add(textField3);


        JButton button2 = new JButton("Disconnect");
        button2.setFocusPainted(false);
        button2.setBounds(584,11,121, 28);
        panelConnect.add(button2);

        JButton button1 = new JButton("Connect");
        button1.setFocusPainted(false);
        button1.setBounds(477,11,91, 28);
        panelConnect.add(button1);

        button2.setEnabled(false);
        button1.setEnabled(true);
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (button1.isEnabled() == false)return;
                super.mouseClicked(e);
                try {
                    plc.setIpAddress(textField1.getText());
                    plc.setPort(Integer.parseInt(textField2.getText()));
                    plc.setConnectionID((byte) Integer.parseInt(textField3.getText()));

                    OperateResult connect = plc.ConnectServer();
                    if(connect.IsSuccess){
                        JOptionPane.showMessageDialog(
                                null,
                                "Connect Success",
                                "Result",
                                JOptionPane.PLAIN_MESSAGE);
                        button2.setEnabled(true);
                        button1.setEnabled(false);
                        userControlReadWriteDevice.SetReadWriteNet(plc, defaultAddress, 10);
                    }
                    else {
                        JOptionPane.showMessageDialog(
                                null,
                                "Connect Failed:" + connect.ToMessageShowString(),
                                "Result",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
                catch (Exception ex){
                    JOptionPane.showMessageDialog(
                            null,
                            "Connect Failed\r\nReason:"+ex.getMessage(),
                            "Result",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        button2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (!button2.isEnabled()) return;
                if(plc !=null){
                    plc.ConnectClose();
                    button1.setEnabled(true);
                    button2.setEnabled(false);
                    userControlReadWriteDevice.setEnabled(false);
                }
            }
        });


        panel.add(panelConnect);
    }

}
