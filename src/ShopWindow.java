/*
Name: Devin Vanzant
Course: CNT 4717 - Spring 2021
Assignment title: Project 1 - Event-driven Enterprise Simulation
Date: Sunday January 31, 2021
 */
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.*;
import java.text.*;

public class ShopWindow extends JFrame
{
    // Each element in the shop window JFrame
    private JPanel mainPanel;
    private JLabel labelNumItems;
    private JLabel labelItemId;
    private JLabel labelQuantity;
    private JLabel labelInfo;
    private JLabel labelSubtotal;
    private JTextField textNumItems;
    private JTextField textItemId;
    private JTextField textQuantity;
    private JTextField textInfo;
    private JTextField textSubtotal;
    private JButton processExit;
    private JButton buttonNew;
    private JButton buttonFinish;
    private JButton buttonConfirm;
    private JButton buttonProcess;
    private JButton buttonView;

    // Global time var
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy, HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();

    // Global counter vars
    int itemCnt = 1;
    int addedItems = 0;
    double finalPrice = 0.00;

    // Global containers for nextLine string stored in different ways
    ArrayList<Double> orderTotal = new ArrayList<>();
    ArrayList<String> receipt = new ArrayList<>();
    ArrayList<String> viewOrder = new ArrayList<>();
    String retStrWithCommas;
    StringBuilder finalReceipt = new StringBuilder();
    ArrayList<String> appendedTransaction = new ArrayList<>();

    File file = new File("inventory.txt");

    public ShopWindow(String title)
    {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();

        // Initial settings for buttons
        buttonConfirm.setEnabled(false);
        buttonView.setEnabled(false);
        buttonFinish.setEnabled(false);

        // Initial labels with appropriate item count
        labelItemId.setText("ID of item #" + itemCnt + ":");
        labelQuantity.setText("Quantity of item #" + itemCnt + ":");
        labelInfo.setText("Item #" + itemCnt + " info:");
        labelSubtotal.setText("Order subtotal for " + addedItems + " item(s):");
        buttonProcess.setText("Process Item #" + itemCnt);
        buttonConfirm.setText("Confirm Item #" + itemCnt);

        // These text boxes should never be editable by the user
        textInfo.setEditable(false);
        textSubtotal.setEditable(false);

        // Will search for item by ID and display info if found
        buttonProcess.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Fail condition, something is empty
                if (textQuantity.getText().isEmpty() || textItemId.getText().isEmpty()
                        || textNumItems.getText().isEmpty())
                {
                    JOptionPane.showMessageDialog(mainPanel, "One or more field(s) are blank!");
                    return;
                }

                // Something was found, disable inputs, assign arguments of method to text inputs for searchId
                textNumItems.setEditable(false);
                textItemId.setEditable(false);
                textQuantity.setEditable(false);

                String idInput = textItemId.getText();
                String itemQuantity = textQuantity.getText();
                int itemCount = Integer.parseInt(textNumItems.getText());

                // Try catch blocks to surround method with Scanner
                try
                {
                    searchId(idInput, itemQuantity);
                }
                catch (IOException ioException)
                {
                    ioException.printStackTrace();
                }

            }
        });

        // After processing, button will add item to transaction
        buttonConfirm.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Confirmation window, then change buttons and text boxes accordingly
                JOptionPane.showMessageDialog(mainPanel, "Item #" + itemCnt + " accepted.");
                buttonConfirm.setEnabled(false);
                buttonProcess.setEnabled(true);
                buttonFinish.setEnabled(true);
                buttonView.setEnabled(true);
                textItemId.setText("");
                textQuantity.setText("");

                // Intellij with another autoformmated enhanced for-loop
                for (Double aDouble : orderTotal) {
                    finalPrice += (double) aDouble;
                }

                // To avoid rounding issues and force a $#.## format
                DecimalFormat dc = new DecimalFormat("0.00");
                String formattedPriceFinal = dc.format(finalPrice);
                textSubtotal.setText("$" + formattedPriceFinal);

                textItemId.setEditable(true);
                textQuantity.setEditable(true);

                addedItems++;
                itemCnt++;
                finalPrice = 0.00;

                // Update labels
                labelItemId.setText("ID of item #" + itemCnt + ":");
                labelQuantity.setText("Quantity of item #" + itemCnt + ":");
                labelInfo.setText("Item #" + itemCnt + " info:");
                labelSubtotal.setText("Order subtotal for " + addedItems + " item(s):");
                buttonProcess.setText("Process Item #" + itemCnt);
                buttonConfirm.setText("Confirm Item #" + itemCnt);

                // If you hit the end of the transaction, disable buttons
                if (addedItems == Integer.parseInt(textNumItems.getText()))
                {
                    buttonProcess.setEnabled(false);
                    textItemId.setEditable(false);
                    textQuantity.setEditable(false);
                }
            }
        });

        // Simply view what's been added to the transaction so far
        buttonView.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(null, viewOrder.toArray());
            }
        });

        // The last button they will click after a successful transaction
        buttonFinish.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (int i = 0; i < viewOrder.toArray().length; i++)
                {
                    // Try catch for Writer
                    try
                    {
                        appendToFile(appendedTransaction.get(i));
                    }
                    catch (IOException ioException)
                    {
                        ioException.printStackTrace();
                    }
                }

                DecimalFormat df = new DecimalFormat("#.##");

                // Ooh fancy, IntelliJ automatically formats enhanced for loops :o
                for (Double price : orderTotal)
                {
                    finalPrice += (double) price;
                }

                // Very interesting workaround I implemented to help display the transaction properly in the window :)
                String willThisWork = Arrays.toString(viewOrder.toArray()).replace("[", "")
                        . replace("]", ""). replace(", ", "");

                // The window can be displayed as just one very large string, StringBuilder helps me avoid casting issues
                finalReceipt.append("Date: " + dtf.format(now) + " EST\n\n")
                    .append("Number of item(s): " + (itemCnt - 1) + "\n\n")
                    .append("Item # / ID / Title / Price / Qty / Disc % / Subtotal:\n\n")
                    .append(willThisWork + "\n\n\n")
                    .append("Order subtotal:    $" + Double.valueOf(df.format(finalPrice)) + "\n\n")
                    .append("Tax rate:    6%\n\n")
                    .append("Tax amount:    $" + calcTax(finalPrice) + "\n\n")
                    .append("Order total:    $" + calcTotal(finalPrice) + "\n\n")
                    .append("Thanks for shopping at Nile Dot Com!");

                    // Actually displays the window
                    JOptionPane.showMessageDialog(null, finalReceipt);
                    System.exit(0);
            }
        });

        // Wipe everything, start from scratch
        buttonNew.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                addedItems = 0;
                itemCnt = 1;

                labelItemId.setText("ID of item #" + itemCnt + ":");
                labelQuantity.setText("Quantity of item #" + itemCnt + ":");
                labelInfo.setText("Item #" + itemCnt + " info:");
                labelSubtotal.setText("Order subtotal for " + addedItems + " item(s):");

                buttonProcess.setEnabled(true);
                buttonConfirm.setEnabled(false);
                buttonView.setEnabled(false);
                buttonFinish.setEnabled(false);

                textNumItems.setEditable(true);
                textItemId.setEditable(true);
                textQuantity.setEditable(true);
                textNumItems.setText("");
                textItemId.setText("");
                textQuantity.setText("");
                textInfo.setText("");
                textSubtotal.setText("");

                orderTotal.clear();
                receipt.clear();
                viewOrder.clear();
            }
        });

        // Just a simple quit button, no prompt needed
        processExit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Successful exit!");
                System.exit(0);
            }
        });
    }

    // Scans through inventory.txt to find a matching ID if applicable
    public void searchId(String idInput, String itemQuantity) throws IOException
    {
        try
        {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine())
            {
                // Reads in items as a whole string, splits them by their commas
                String itemInfo = sc.nextLine();
                String [] splitInfo = itemInfo.split(", ");

                // After splitting, assign Strings to parts of the split
                String id = splitInfo[0];
                String item = splitInfo[1];
                String inStock = splitInfo[2];
                double price = Double.parseDouble(splitInfo[3]);
                //int intItemCnt = Integer.parseInt(itemQuantity);

                // Item is out of stock and is valid
                if (id.equalsIgnoreCase(idInput) && inStock.equalsIgnoreCase("false"))
                {
                    JOptionPane.showMessageDialog(mainPanel, "Item " + id + " is out of stock!");
                    textItemId.setEditable(true);
                    textQuantity.setEditable(true);
                    return;
                }

                if (!idInput.isEmpty() && !itemQuantity.isEmpty())
                {
                    // Item match!
                    if (idInput.equalsIgnoreCase(id))
                    {
                        DecimalFormat dc = new DecimalFormat("0.00");
                        double discount = discountCalc(itemQuantity);
                        double finalPrice = finalPriceCalc(price, itemQuantity, discount);
                        String formattedPriceFinal = dc.format(finalPrice);
                        String formattedPrice = dc.format(price);

                        String retStr = id + " " + item + " $" + formattedPrice + " " +
                                itemQuantity + " " + (int)(discount * 100) + "% " + "$" + formattedPriceFinal + "\n";
                        retStrWithCommas = id + ", " + item + ", " + formattedPrice + ", " +
                                itemQuantity + ", " + discount + ", " + "$" + formattedPriceFinal + ", ";

                        textInfo.setText(retStr);
                        receipt.add(retStr);
                        appendedTransaction.add(retStrWithCommas);
                        String appStr = itemCnt + ". " + retStr;
                        viewOrder.add(appStr);
                        buttonProcess.setEnabled(false);
                        buttonConfirm.setEnabled(true);
                        orderTotal.add(finalPrice);

                        return;
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(mainPanel, "One or more field(s) are blank!");
                    return;
                }
            }
            // If ID cannot match to anything in the list
            JOptionPane.showMessageDialog(mainPanel, "Item ID " + idInput + " is not in file.");
            textItemId.setEditable(true);
            textQuantity.setEditable(true);
        }
        catch (FileNotFoundException e)
        {
            JOptionPane.showMessageDialog(mainPanel, "Cannot find inventory.txt");
        }
    }

    // This will generate an ID for each transaction
    public static StringBuilder generateTransactionId()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy:HH:mm");
        LocalDateTime now = LocalDateTime.now();
        StringBuilder timeStr = new StringBuilder();

        String [] time = dtf.format(now).split(":");
        int day  = Integer.parseInt(time[0]);
        int mon  = Integer.parseInt(time[1]);
        int year = Integer.parseInt(time[2]);
        int hour = Integer.parseInt(time[3]);
        int min  = Integer.parseInt(time[4]);

        timeStr = timeStr.append(day).append(mon).append(year).append(hour).append(min);

        return timeStr;
    }

    // Calculates tax amount
    public static double calcTax(double finalPrice)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        double tax = finalPrice * 0.06;
        tax = Double.valueOf(df.format(tax));
        return tax;
    }

    // Calculates total plus tax
    public static double calcTotal(double finalPrice)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        double tax = finalPrice * 1.06;
        tax = Double.valueOf(df.format(tax));
        return tax;
    }

    // Creates transactions.txt if not already there
    public static void createFile()
    {
        try
        {
            File transaction = new File("transactions.txt");
            if (transaction.createNewFile()) {
                System.out.println("Created new transactions file");
            }
            else
            {
                System.out.println("Exists already");
            }
        }
        catch (IOException e)
        {
            System.out.println("Error.");
        }
    }

    // Calculates how much the discount should be based on quantity
    public static double discountCalc(String intItemCnt)
    {
        int test = Integer.parseInt(intItemCnt);

        if (test >= 1 && test <= 4)
        {
            return 0.0;
        }
        else if (test >= 5 && test <= 9)
        {
            return 0.1;
        }
        else if (test >= 10 && test <= 14)
        {
            return 0.15;
        }
        else if (test >= 15)
        {
            return 0.2;
        }
        else
        {
            return 0.0;
        }
    }

    // Calculates the final price given discount
    public static double finalPriceCalc(double price, String intItemCnt, double discount)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        int test = Integer.parseInt(intItemCnt);
        double total = (1.00 - discount) * (price * test);
        total = Double.valueOf(df.format(total));
        return total;
    }

    // Writes to the transactions.txt file
    public static void appendToFile(String retStr) throws IOException
    {
        StringBuilder transId = generateTransactionId();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy, HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();


        FileWriter fr = new FileWriter("transactions.txt", true);
        BufferedWriter br = new BufferedWriter(fr);
        br.write(transId + ", " + retStr + dtf.format(now) + " EST\n");

        br.close();
        fr.close();
    }

    // The starter
    public static void main(String[] args)
    {
        JFrame frame = new ShopWindow("Nile Dot Com");
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
