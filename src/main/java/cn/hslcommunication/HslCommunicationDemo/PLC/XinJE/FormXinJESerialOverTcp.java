package cn.hslcommunication.HslCommunicationDemo.PLC.XinJE;

import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Profinet.XINJE.XinJESerialOverTcp;
import HslCommunication.Profinet.XINJE.XinJESeries;
import cn.hslcommunication.HslCommunicationDemo.DemoUtils;
import cn.hslcommunication.HslCommunicationDemo.UserControlReadWriteDevice;
import cn.hslcommunication.HslCommunicationDemo.UserControlReadWriteHead;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FormXinJESerialOverTcp extends JPanel {

    public FormXinJESerialOverTcp(JTabbedPane tabbedPane){
        setLayout(null);
        add( new UserControlReadWriteHead("Modbus Rtu", tabbedPane, this));
        AddConnectSegment(this);
        plc = new XinJESerialOverTcp();

        userControlReadWriteDevice = DemoUtils.CreateDevicePanel(this);
        userControlReadWriteDevice.setEnabled(false);
    }

    private XinJESerialOverTcp plc = null;
    private String defaultAddress = "D100";
    private UserControlReadWriteDevice userControlReadWriteDevice = null;


    public void AddConnectSegment(JPanel panel){
        JPanel panelConnect = DemoUtils.CreateConnectPanel(panel);

        JLabel label1 = new JLabel("Ip：");
        label1.setBounds(8, 17,56, 17);
        panelConnect.add(label1);

        JTextField textField1 = new JTextField();
        textField1.setBounds(62,14,106, 23);
        textField1.setText("192.168.0.10");
        panelConnect.add(textField1);

        JLabel label2 = new JLabel("Port：");
        label2.setBounds(184, 17,56, 17);
        panelConnect.add(label2);

        JTextField textField2 = new JTextField();
        textField2.setBounds(238,14,61, 23);
        textField2.setText("502");
        panelConnect.add(textField2);

        JLabel label3 = new JLabel("Station：");
        label3.setBounds(318, 17,56, 17);
        panelConnect.add(label3);

        JTextField textField3 = new JTextField();
        textField3.setBounds(372,14,40, 23);
        textField3.setText("1");
        panelConnect.add(textField3);

        JCheckBox checkBox1 = new JCheckBox("Start from 0?");
        checkBox1.setBounds(427,16,106, 21);
        checkBox1.setSelected(true);
        panelConnect.add(checkBox1);

        JComboBox<DataFormat> comboBox1 = new JComboBox<>();
        comboBox1.setBounds(538,13,61, 25);
        comboBox1.addItem(DataFormat.ABCD);
        comboBox1.addItem(DataFormat.BADC);
        comboBox1.addItem(DataFormat.CDAB);
        comboBox1.addItem(DataFormat.DCBA);
        comboBox1.setSelectedItem(0);
        panelConnect.add(comboBox1);

        JLabel label4 = new JLabel("Series:");
        label4.setBounds(610,14,40, 23);
        panelConnect.add(label4);

        JComboBox<XinJESeries> comboBox_Series = new JComboBox<>();
        comboBox_Series.setBounds(660,13,61, 25);
        comboBox_Series.addItem(XinJESeries.XC);
        comboBox_Series.addItem(XinJESeries.XD);
        comboBox_Series.addItem(XinJESeries.XL);
        comboBox_Series.setSelectedItem(0);
        panelConnect.add(comboBox_Series);

        JButton button2 = new JButton("Disconnect");
        button2.setFocusPainted(false);
        button2.setBounds(850,11,121, 28);
        panelConnect.add(button2);

        JButton button1 = new JButton("Connect");
        button1.setFocusPainted(false);
        button1.setBounds(752,11,91, 28);
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
                    plc.setAddressStartWithZero(checkBox1.isSelected());
                    plc.setDataFormat((DataFormat) comboBox1.getSelectedItem());
                    plc.Series = (XinJESeries) comboBox_Series.getSelectedItem();

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
