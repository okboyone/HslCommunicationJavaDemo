package cn.hslcommunication.HslCommunicationDemo.PLC.Melsec;

import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.MelsecA3CNetOverTcp;
import cn.hslcommunication.HslCommunicationDemo.DemoUtils;
import cn.hslcommunication.HslCommunicationDemo.UserControlReadWriteDevice;
import cn.hslcommunication.HslCommunicationDemo.UserControlReadWriteHead;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FormMelsecA3CNetOverTcp extends JPanel {
    public FormMelsecA3CNetOverTcp(JTabbedPane tabbedPane){
        setLayout(null);
        add( new UserControlReadWriteHead("MelsecA3CNetOverTcp", tabbedPane, this));
        AddConnectSegment(this);
        melsec = new MelsecA3CNetOverTcp();

        fxLinksControl = AddFunction();
        userControlReadWriteDevice = DemoUtils.CreateDevicePanel(this);
        userControlReadWriteDevice.AddSpecialFunctionTab(fxLinksControl, false, "FxLinksFunction");
        userControlReadWriteDevice.setEnabled(false);
        DemoUtils.SetPanelEnabled(fxLinksControl, false);
    }

    private MelsecA3CNetOverTcp melsec = null;
    private String defaultAddress = "D100";
    private UserControlReadWriteDevice userControlReadWriteDevice = null;
    private JPanel fxLinksControl;

    public void AddConnectSegment(JPanel panel){
        JPanel panelConnect = DemoUtils.CreateConnectPanel(panel);

        JLabel label1 = new JLabel("Ip：");
        label1.setBounds(8, 17,30, 17);
        panelConnect.add(label1);

        JTextField textField1 = new JTextField();
        textField1.setBounds(42,14,126, 23);
        textField1.setText("192.168.0.10");
        panelConnect.add(textField1);

        JLabel label2 = new JLabel("Port：");
        label2.setBounds(170, 17,56, 17);
        panelConnect.add(label2);

        JTextField textField2 = new JTextField();
        textField2.setBounds(220,14,60, 23);
        textField2.setText("2000");
        panelConnect.add(textField2);

        JLabel label3 = new JLabel("Station：");
        label3.setBounds(280, 17,65, 17);
        panelConnect.add(label3);

        JTextField textField3 = new JTextField();
        textField3.setBounds(340,14,50, 23);
        textField3.setText("0");
        panelConnect.add(textField3);

        JCheckBox checkBox1 = new JCheckBox("SumCheck?");
        checkBox1.setBounds(400, 6, 100, 21);
        checkBox1.setSelected(true);
        panelConnect.add(checkBox1);

        JCheckBox checkBox2 = new JCheckBox("EnableWriteBitToWordRegister?");
        checkBox2.setBounds(400, 30, 250, 21);
        checkBox2.setSelected(false);
        panelConnect.add(checkBox2);

        JLabel label5 = new JLabel("Format：");
        label5.setBounds(610, 17,70, 17);
        panelConnect.add(label5);


        JComboBox<Integer> comboBox1 = new JComboBox<>();
        comboBox1.setBounds(680,13,60, 25);
        comboBox1.addItem(1);
        comboBox1.addItem(2);
        comboBox1.addItem(3);
        comboBox1.addItem(4);
        comboBox1.setSelectedItem(1);
        panelConnect.add(comboBox1);

        JButton button2 = new JButton("Disconnect");
        button2.setFocusPainted(false);
        button2.setBounds(850,11,121, 28);
        panelConnect.add(button2);

        JButton button1 = new JButton("Connect");
        button1.setFocusPainted(false);
        button1.setBounds(750,11,91, 28);
        panelConnect.add(button1);

        button2.setEnabled(false);
        button1.setEnabled(true);
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!button1.isEnabled())return;
                super.mouseClicked(e);
                try {
                    melsec.setIpAddress(textField1.getText());
                    melsec.setPort(Integer.parseInt(textField2.getText()));
                    melsec.Station = Byte.parseByte(textField3.getText());
                    melsec.SumCheck = checkBox1.isSelected();
                    melsec.EnableWriteBitToWordRegister = checkBox2.isSelected();
                    melsec.Format = (int)comboBox1.getSelectedItem();

                    OperateResult connect = melsec.ConnectServer();
                    if(connect.IsSuccess){
                        JOptionPane.showMessageDialog(
                                null,
                                "Connect Success",
                                "Result",
                                JOptionPane.PLAIN_MESSAGE);
                        button2.setEnabled(true);
                        button1.setEnabled(false);
                        userControlReadWriteDevice.SetReadWriteNet(melsec, defaultAddress, 10);
                        DemoUtils.SetPanelEnabled(fxLinksControl, true);
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
                if (button2.isEnabled() == false) return;
                if(melsec !=null){
                    melsec.ConnectClose();
                    button1.setEnabled(true);
                    button2.setEnabled(false);
                    userControlReadWriteDevice.setEnabled(false);
                    DemoUtils.SetPanelEnabled(fxLinksControl, false);
                }
            }
        });


        panel.add(panelConnect);
    }

    public JPanel AddFunction(){
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JButton button_start = new JButton("Run PLC");
        button_start.setBounds(10, 5, 120, 28);
        button_start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                OperateResult op = melsec.RemoteRun();
                if (op.IsSuccess){
                    JOptionPane.showMessageDialog(
                            null,
                            "Run Success",
                            "Result",
                            JOptionPane.PLAIN_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Run failed: " + op.ToMessageShowString(),
                            "Result",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(button_start);

        JButton button_stop = new JButton("Stop PLC");
        button_stop.setBounds(140, 5, 120, 28);
        button_stop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                OperateResult op = melsec.RemoteStop();
                if (op.IsSuccess){
                    JOptionPane.showMessageDialog(
                            null,
                            "Stop Success",
                            "Result",
                            JOptionPane.PLAIN_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Stop failed: " + op.ToMessageShowString(),
                            "Result",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(button_stop);

        JButton button_read_type = new JButton("PLC Type");
        button_read_type.setBounds(270, 5, 120, 28);
        button_read_type.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                OperateResultExOne<String> op = melsec.ReadPlcType();
                if (op.IsSuccess){
                    JOptionPane.showMessageDialog(
                            null,
                            "Read Success: " + op.Content,
                            "Result",
                            JOptionPane.PLAIN_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Read failed: " + op.ToMessageShowString(),
                            "Result",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(button_read_type);

        return panel;
    }
}
