package br.com.ifpr;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

public class CrudApp extends JFrame {
	//Dados para conexão com o Banco de Dados
    private static final String DB_URL = "jdbc:mysql://localhost/agenda";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "260304";

    private DefaultTableModel tableModel;
    private JTextField idField;
    private JTextField nomeField;
    private JTextField idadeField;
    private JTextField telefoneField;
    private JTextField searchField;
    private JTable table;

    public CrudApp() {
        super("Aplicativo de Contatos");

        // Configurar a tabela com as colunas
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Nome");
        tableModel.addColumn("Idade");
        tableModel.addColumn("Telefone");

        table = new JTable(tableModel);

        // Criar os campos de texto
        idField = new JTextField();
        nomeField = new JTextField();
        idadeField = new JTextField();
        telefoneField = new JTextField();
        searchField = new JTextField();

        // Criação dos botões
        JButton cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarContato();
            }
        });

        JButton atualizarButton = new JButton("Atualizar");
        atualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarContato();
            }
        });

        JButton excluirButton = new JButton("Excluir");
        excluirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirContato();
            }
        });

        // Criação de painéis
        JPanel attributesPanel = new JPanel(new GridBagLayout());
        attributesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 5);
        attributesPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        attributesPanel.add(idField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        attributesPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        attributesPanel.add(nomeField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        attributesPanel.add(new JLabel("Idade:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        attributesPanel.add(idadeField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        attributesPanel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        attributesPanel.add(telefoneField, gbc);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.add(cadastrarButton);
        buttonsPanel.add(atualizarButton);
        buttonsPanel.add(excluirButton);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(new JLabel("Pesquisar por ID: "), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        searchPanel.add(searchField, gbc);

        //  imagem ( O ícone de usuário)
        ImageIcon imageIcon = new ImageIcon("C:\\\\Users\\\\dcart\\\\Downloads\\\\imagemcrud.png");
        Image image = imageIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 5, 0, 0);
        attributesPanel.add(imageLabel, gbc);

        // Configuração da janela principal
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(attributesPanel, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(table), gbc);

        gbc.gridy++;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(buttonsPanel, gbc);

        gbc.gridy++;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(searchPanel, gbc);

        // barra de pesquisa
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    pesquisarContato();
                }
            }
        });

        // Mostra todos os contatos que já foram cadastros no banco
        carregarContatos();
    }
    	// Método para cadastrar um novo usuário
    private void cadastrarContato() {
        String nome = nomeField.getText();
        int idade = Integer.parseInt(idadeField.getText());
        String telefone = telefoneField.getText();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String query = "INSERT INTO contatos (nome, idade, telefone) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, nome);
            statement.setInt(2, idade);
            statement.setString(3, telefone);
            statement.executeUpdate();
            statement.close();
            conn.close();

            limparCampos();
            carregarContatos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    	// Método para atualizar um usuario
    private void atualizarContato() {
        String id = idField.getText();
        String nome = nomeField.getText();
        int idade = Integer.parseInt(idadeField.getText());
        String telefone = telefoneField.getText();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String query = "UPDATE contatos SET nome = ?, idade = ?, telefone = ? WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, nome);
            statement.setInt(2, idade);
            statement.setString(3, telefone);
            statement.setString(4, id);
            statement.executeUpdate();
            statement.close();
            conn.close();

            limparCampos();
            carregarContatos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    	// Método para excluir contato
    private void excluirContato() {
        String id = idField.getText();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String query = "DELETE FROM contatos WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);
            statement.executeUpdate();
            statement.close();
            conn.close();

            limparCampos();
            carregarContatos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    	// pesquisa pelo ID
    private void pesquisarContato() {
        String id = searchField.getText();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String query = "SELECT * FROM contatos WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                idField.setText(resultSet.getString("id"));
                nomeField.setText(resultSet.getString("nome"));
                idadeField.setText(resultSet.getString("idade"));
                telefoneField.setText(resultSet.getString("telefone"));
            } else {
                JOptionPane.showMessageDialog(this, "Contato não encontrado");
            }

            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Carrega o contatos na tabela
    private void carregarContatos() {
        // Limpar tabela
        tableModel.setRowCount(0);

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            Statement statement = conn.createStatement();
            String query = "SELECT * FROM contatos";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String nome = resultSet.getString("nome");
                String idade = resultSet.getString("idade");
                String telefone = resultSet.getString("telefone");

                tableModel.addRow(new String[]{id, nome, idade, telefone});
            }

            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Método para limpar os campos após cadastrar um user
    private void limparCampos() {
        idField.setText("");
        nomeField.setText("");
        idadeField.setText("");
        telefoneField.setText("");
        searchField.setText("");
    }
    // Método Main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CrudApp().setVisible(true);
            }
        });
    }
}
