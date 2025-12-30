package functionality;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

import classes.*;

/**
 * This class manages the core inventory logic.
 * <p>
 * It handles product registration, removal, searching, restocking,
 * sales processing, report generation, and PDF exporting.
 * <p>
 * This class does not handle user menus directly; instead, it provides
 * protected methods to be used by higher-level controller classes.
 *
 * @author Sebastian T.
 * @version 1.0
 * @since 2025-12-25
 */
public class Inventory {

    /**
     * Stores all registered products in the inventory.
     */
    protected final static ArrayList<Product> inventory = new ArrayList<>();

    /**
     * Stores all registered sales, grouped by product.
     */
    protected final static ArrayList<Sale> totalSales = new ArrayList<>();

    /**
     * Scanner used for console input.
     */
    protected static Scanner sc = new Scanner(System.in);

    /**
     * Current system date used for report generation.
     */
    private static LocalDate currentDate = LocalDate.now();

    // ----------------------------- Product introduction and registration -----------------------------

    /**
     * Receives multiple products from user input.
     * <p>
     * Prompts the user for product details such as name, category,
     * price, VAT, code, units, and minimum threshold.
     *
     * @param quantity number of products to receive
     * @return an array of {@link Product} objects
     * @throws java.util.InputMismatchException if invalid input type is entered
     */
    protected Product[] receiveProducts(int quantity) {
        String productName, category, code;
        float price, VAT;
        int productUnits, minimumThreshold;

        Product[] products = new Product[quantity];
        System.out.println();

        for (int i = 0; i < quantity; i++) {
            System.out.println("Product #" + (i + 1));

            System.out.println("Enter the product name: ");
            productName = sc.nextLine().strip();

            System.out.println("Enter its category (e.g., accessory, electronics...): ");
            category = sc.nextLine().strip();

            System.out.println("Enter the price of this product: ");
            price = sc.nextInt();

            System.out.println("Enter the VAT percentage of the product: ");
            VAT = sc.nextFloat();

            System.out.println("Enter the product code: ");
            sc.nextLine();
            code = sc.nextLine().strip();

            System.out.println("Enter the product units: ");
            productUnits = sc.nextInt();

            if (productUnits <= 0 || VAT < 0 || price <= 0) {
                System.out.println("Error: You cannot enter negative quantities or equal to zero.");
                quantity--;
                i--;
                continue;
            }

            System.out.println("Enter the minimum threshold to restock the product: ");
            minimumThreshold = sc.nextInt();

            products[i] = new Product(productName, category, price, VAT, code, productUnits, minimumThreshold);
            System.out.println();
            sc.nextLine();
        }

        return products;
    }

    /**
     * Registers products into the inventory.
     * <p>
     * If a product already exists, its available units are increased.
     * Otherwise, the product is added as new.
     *
     * @param products array of products to register
     */
    protected void registerProductsInventory(Product[] products) {
        for (Product product : products) {
            boolean exists = false;

            for (Product value : inventory) {
                if (product.getProductName().equalsIgnoreCase(value.getProductName())) {
                    value.setAvailableUnits(value.getAvailableUnits() + product.getAvailableUnits());
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                inventory.add(product);
            }
        }
    }

    /**
     * Removes a product from the inventory by its name.
     *
     * @param productName name of the product to remove
     */
    protected void removeProduct(String productName) {
        for (Product product : inventory) {
            if (product.getProductName().equalsIgnoreCase(productName)) {
                inventory.remove(product);
                System.out.println("Product removed successfully.");
                return;
            }
        }

        System.out.println("Product not found.");
    }

    /**
     * Searches for a product in the inventory by name.
     *
     * @param productName name of the product to search
     */
    protected void searchProduct(String productName) {
        for (Product product : inventory) {
            if (product.getProductName().equalsIgnoreCase(productName)) {
                System.out.printf("Product found:%n");
                System.out.printf("Name: %s%n", product.getProductName());
                System.out.printf("Category: %s%n", product.getCategory());
                System.out.printf("Price: %.2f%n", product.getSalePrice());
                System.out.printf("Available units: %d%n", product.getAvailableUnits());
                return;
            }
        }

        System.out.println("Product not found.");
    }

    protected void updateProduct() {
        System.out.println("Enter the product name to update: ");
        sc.nextLine();
        String productName = sc.nextLine().strip();

        for (Product product : inventory) {
            if (product.getProductName().equalsIgnoreCase(productName)) {
                System.out.println("What do you want to update?");
                System.out.println("1. Name of the product");
                System.out.println("2. Category");
                System.out.println("3. Code");
                System.out.println("4. Price");
                System.out.println("5. VAT");
                System.out.println("6. Minimum threshold");

                System.out.print("Option: ");
                int option = sc.nextInt();

                while (true) {
                    System.out.print("Operation: ");

                    try {
                        option = sc.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("Error: Please enter a valid number.");
                        sc.nextLine();
                        continue;
                    }

                    if (option < 1 || option > 6) {
                        System.out.println("Please enter a number between 1 and 6.");
                    } else {
                        break;
                    }
                }
                System.out.println();

                switch (option) {
                    case 1:
                        System.out.print("Type the new name for the product: ");
                        String name = sc.nextLine();

                        product.setProductName(name);
                        break;
                    case 2:
                        System.out.print("Type the new category for the product: ");
                        String category = sc.nextLine();

                        product.setCategory(category);
                        break;
                    case 3:
                        System.out.print("Type the new code for the product: ");
                        String code = sc.nextLine();

                        product.setCode(code);
                        break;
                }


            }
        }
        System.out.println("Product not found.");
    }

    /**
     * Restocks existing products in the inventory.
     *
     * @param productQuantity number of products to restock
     * @throws java.util.InputMismatchException if invalid input type is entered
     */
    protected void restockInventory(int productQuantity) {
        int unitsToRestock;
        String productName;

        for (int i = 0; i < productQuantity; i++) {
            boolean productFound = false;

            System.out.println("What product are you going to restock?");
            sc.nextLine();
            productName = sc.nextLine();

            System.out.println("How many units are you going to add?");
            unitsToRestock = sc.nextInt();

            if (unitsToRestock <= 0) {
                System.out.println("Error: You cannot enter negative quantities or equal to zero.");
                continue;
            }

            for (Product product : inventory) {
                if (productName.equalsIgnoreCase(product.getProductName())) {
                    product.setAvailableUnits(product.getAvailableUnits() + unitsToRestock);
                    productFound = true;
                    break;
                }
            }

            if (!productFound) {
                System.out.println("Error: Product not found.");
            }
        }
    }

    // ----------------------------- Sales management -----------------------------

    /**
     * Registers a sale and accumulates profit per product.
     *
     * @param productName name of the sold product
     * @param saleCost    total cost of the sale
     */
    protected void saleRegistration(String productName, float saleCost) {
        boolean productSaleExists = false;
        Sale newSale = new Sale(productName, saleCost);

        for (Sale totalSale : totalSales) {
            if (newSale.getProductName().equalsIgnoreCase(totalSale.getProductName())) {
                totalSale.setTotalProfit(totalSale.getTotalProfit() + newSale.getTotalProfit());
                productSaleExists = true;
                break;
            }
        }

        if (!productSaleExists) {
            totalSales.add(newSale);
        }

        totalSales.sort(Comparator.comparing(Sale::getTotalProfit).reversed());
    }

    /**
     * Processes the sale of one or more products.
     *
     * @param productQuantity number of different products to sell
     * @throws java.util.InputMismatchException if invalid input type is entered
     */
    protected void sellProduct(int productQuantity) {
        String productName;
        int unitsToBuy;
        float saleCost, totalCost = 0;

        System.out.println();

        for (int i = 0; i < productQuantity; i++) {
            boolean productFound = false, isAvailable = true;

            System.out.println("Product #" + (i + 1));
            System.out.println("Enter the product name: ");
            productName = sc.nextLine();

            System.out.println("Enter the quantity of units to buy: ");
            unitsToBuy = sc.nextInt();

            if (unitsToBuy <= 0) {
                System.out.println("Error: You cannot enter negative quantities or equal to zero.");
                productQuantity--;
                i--;
                continue;
            }
            sc.nextLine();

            for (Product product : inventory) {
                if (productName.equalsIgnoreCase(product.getProductName())) {
                    if (product.getAvailableUnits() == 0 || unitsToBuy > product.getAvailableUnits()) {
                        isAvailable = false;
                        break;
                    }

                    product.setAvailableUnits(product.getAvailableUnits() - unitsToBuy);
                    saleCost = ((product.getSalePrice() * unitsToBuy)
                            * ((100 + product.getVATPercentage()) / 100));
                    totalCost += saleCost;

                    saleRegistration(productName, saleCost);
                    productFound = true;
                    break;
                }
            }

            if (!productFound) {
                System.out.printf("Error: The product %s was not found or is not available.%n", productName);
            }

            if (!isAvailable) {
                System.out.println("Error: Insufficient stock available.");
            }
        }

        if (!totalSales.isEmpty()) {
            System.out.printf("The total cost of this purchase (VAT included) is: %.2f%n", totalCost);
        }
    }

    // ----------------------------- Reports and listings -----------------------------

    /**
     * Prints a summarized inventory report to the console.
     */
    protected void printInventoryReport() {
        System.out.printf("Report date: %s%n", currentDate);
        int totalProductCounter = 0;

        System.out.printf("%-25s %-20s %-22s %-15s%n",
                "Product name", "Product code", "Available units", "Minimum threshold");

        for (Product product : inventory) {
            System.out.printf("%-25s %-20s %-22d %-15d%n",
                    product.getProductName(), product.getCode(),
                    product.getAvailableUnits(), product.getMinimumThreshold());

            totalProductCounter += product.getAvailableUnits();
        }

        System.out.printf("Total quantity of products: %d%n", totalProductCounter);

        for (Product product : inventory) {
            if (product.getAvailableUnits() == 0) {
                System.out.printf("Warning! The product \"%s\" has 0 available units.%n",
                        product.getProductName());
            } else if (product.getAvailableUnits() <= product.getMinimumThreshold()) {
                System.out.printf("Warning! The product \"%s\" is below the minimum threshold.%n",
                        product.getProductName());
            }
        }
    }

    /**
     * Prints a sales report to the console.
     */
    protected void printSalesReport() {
        if (totalSales.isEmpty()) {
            System.out.println("There hasn't been any sales made.");
            return;
        }

        System.out.printf("Report date: %s%n", currentDate);
        System.out.printf("%-26s %-30s%n", "Product name", "Total product profits");

        float totalReportProfit = 0;
        for (Sale sale : totalSales) {
            totalReportProfit += sale.getTotalProfit();
            System.out.printf("%-26s %-30.2f%n",
                    sale.getProductName(), sale.getTotalProfit());
        }

        System.out.printf("Total profits: %.2f%n", totalReportProfit);
    }

    /**
     * Prints a full inventory listing to the console.
     */
    protected void printInventoryListing() {
        System.out.printf("Report date: %s%n", currentDate);

        System.out.printf("%-20s %-15s %-12s %-10s %-12s %-10s %-10s%n",
                "Name", "Category", "Price", "VAT (%)", "Code", "Units", "Threshold");

        for (Product product : inventory) {
            System.out.printf("%-20s %-15s %-12.2f %-10.2f %-12s %-10d %-10d%n",
                    product.getProductName(), product.getCategory(),
                    product.getSalePrice(), product.getVATPercentage(),
                    product.getCode(), product.getAvailableUnits(),
                    product.getMinimumThreshold());
        }
    }

    /**
     * Generates the inventory report as a PDF file.
     */
    protected void generateInventoryReportPDF() {
        PDFGenerator.generateInventoryReportPDF(inventory);
    }

    /**
     * Generates the sales report as a PDF file.
     */
    protected void generateSalesReportPDF() {
        PDFGenerator.generateSalesReportPDF(totalSales);
    }

    /**
     * Generates the inventory listing as a PDF file.
     */
    protected void generateInventoryListingReportPDF() {
        PDFGenerator.generateInventoryListingPDF(inventory);
    }

    /**
     * Checks whether the inventory is empty.
     *
     * @return {@code true} if the inventory is empty, {@code false} otherwise
     */
    protected boolean inventoryIsEmpty() {
        if (inventory.isEmpty()) {
            System.out.println("Error: The inventory has no items.");
            return true;
        }
        return false;
    }
}
