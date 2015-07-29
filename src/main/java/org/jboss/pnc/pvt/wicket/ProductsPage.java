package org.jboss.pnc.pvt.wicket;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.kendo.ui.datatable.DataTable;
import com.googlecode.wicket.kendo.ui.datatable.column.CurrencyPropertyColumn;
import com.googlecode.wicket.kendo.ui.datatable.column.IColumn;
import com.googlecode.wicket.kendo.ui.datatable.column.PropertyColumn;
import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.Serializable;
import java.util.*;

/**
 * Created by yyang on 4/30/15.
 */
public class ProductsPage extends TemplatePage{

    public ProductsPage() {
        this("PVT products loaded.");
    }

    public ProductsPage(String info) {
        super(info);
        setActiveMenu("products");
/*
        add(new Link<String>("link-product") {
            @Override
            public void onClick() {
                PageParameters pp = new PageParameters();
                pp.set(0, "a");
                pp.set(1,"b");
                pp.add("c","d");
                setResponsePage(ReleasePage.class, pp);
            }
        });
*/

        add(new Link<String>("link-newproduct") {
            @Override
            public void onClick() {
                PageParameters pp = new PageParameters();
                pp.set(0, "a");
                pp.set(1,"b");
                pp.add("c","d");
                setResponsePage(NewProductPage.class, pp);
            }
        });





        // Form //
        final Form<?> form = new Form<Void>("form");
        this.add(form);

        // DataTable //
        IDataProvider<Product> provider = newDataProvider();
        List<IColumn> columns = newColumnList();

        Options options = new Options();
        options.set("height", 430);
        options.set("pageable", "{ pageSizes: [ 25, 50, 100 ] }");
        //options.set("sortable", true); // already set, as provider IS-A ISortStateLocator
        options.set("groupable", true);
        options.set("columnMenu", true);

        final DataTable<Product> table = new DataTable<Product>("datatable", columns, provider, 25, options);
        form.add(table);

        // Button //
        form.add(new AjaxButton("refresh") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                table.refresh(target);
            }
        });

    }

    private static IDataProvider<Product> newDataProvider()
    {
        return new Product.ProductDataProvider();
    }

    private static List<IColumn> newColumnList()
    {
        List<IColumn> columns = new ArrayList<IColumn>();

        columns.add(new PropertyColumn("ID", "id", 50));
        columns.add(new PropertyColumn("Name", "name"));
        columns.add(new PropertyColumn("Description", "description"));
        columns.add(new CurrencyPropertyColumn("Price", "price", 70));
//		columns.add(new DatePropertyColumn("Date", "date"));
        columns.add(new PropertyColumn("Vendor", "vendor.name"));

        return columns;
    }


    public static class Product implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private int id;
        private String name;
        private String desc;
        private Date date;
        private double price;

        private Vendor vendor;

        public Product(final int id, final String name, final String description, final double price)
        {
            this(id, name, description, price, new Vendor());
        }

        public Product(final int id, final String name, final String description, final double price, Vendor vendor)
        {
            this.id = id;
            this.name = name;
            this.desc = description;
            this.date = new Date();
            this.price = price;
            this.vendor = vendor;
        }

        public int getId()
        {
            return this.id;
        }

        public String getName()
        {
            return this.name;
        }

        public String getDescription()
        {
            return this.desc;
        }

        public Date getDate()
        {
            return this.date;
        }

        public double getPrice()
        {
            return this.price;
        }

        public Vendor getVendor()
        {
            return this.vendor;
        }

        @Override
        public String toString()
        {
            return this.name;
        }

        public static class Vendor implements Serializable
        {
            private static final long serialVersionUID = 1L;

            private String name;

            public Vendor()
            {
                this("noname");
            }

            public Vendor(String name)
            {
                this.name = name;
            }

            public String getName()
            {
                return name;
            }
        }


        public static class ProductDataProvider extends ListDataProvider<Product> implements ISortStateLocator<String>
        {
            private static final long serialVersionUID = 1L;

            private final SingleSortState<String> state = new SingleSortState<String>();



            static ArrayList<Product> list = new ArrayList<Product>();

            static {
                list.add(new Product(1, "Chai", "10 boxes x 20 bags", 18.0000, new Vendor("vendor #1")));
                list.add(new Product(2, "Chang", "24 - 12 oz bottles", 19.0000, new Vendor("vendor #2")));
                list.add(new Product(3, "Aniseed Syrup", "12 - 550 ml bottles", 10.0000, new Vendor("vendor #3")));
                list.add(new Product(4, "Chef Anton's Cajun Seasoning", "48 - 6 oz jars", 22.0000, new Vendor("vendor #4")));
                list.add(new Product(5, "Chef Anton's Gumbo Mix", "36 boxes", 21.3500, new Vendor("vendor #5")));
                list.add(new Product(6, "Grandma's Boysenberry Spread", "12 - 8 oz jars", 25.0000, new Vendor("vendor #1")));
                list.add(new Product(7, "Uncle Bob's Organic Dried Pears", "12 - 1 lb pkgs.", 30.0000, new Vendor("vendor #2")));
                list.add(new Product(8, "Northwoods \"Cranberry\" Sauce", "12 - 12 oz jars", 40.0000, new Vendor("vendor #3")));
                list.add(new Product(9, "Mishi Kobe Niku", "18 - 500 g pkgs.", 97.0000, new Vendor("vendor #4")));
                list.add(new Product(10, "Ikura", "12 - 200 ml jars", 31.0000, new Vendor("vendor #5")));
                list.add(new Product(11, "Queso Cabrales", "1 kg pkg.", 21.0000, new Vendor("vendor #1")));
                list.add(new Product(12, "Queso Manchego La Pastora", "10 - 500 g pkgs.", 38.0000, new Vendor("vendor #2")));
                list.add(new Product(13, "Konbu", "2 kg box", 6.0000, new Vendor("vendor #3")));
                list.add(new Product(14, "Tofu", "40 - 100 g pkgs.", 23.2500, new Vendor("vendor #4")));
                list.add(new Product(15, "Genen Shouyu", "24 - 250 ml bottles", 15.5000, new Vendor("vendor #5")));
                list.add(new Product(16, "Pavlova", "32 - 500 g boxes", 17.4500, new Vendor("vendor #1")));
                list.add(new Product(17, "Alice Mutton", "20 - 1 kg tins", 39.0000, new Vendor("vendor #2")));
                list.add(new Product(18, "Carnarvon Tigers", "16 kg pkg.", 62.5000, new Vendor("vendor #3")));
                list.add(new Product(19, "Teatime Chocolate Biscuits", "10 boxes x 12 pieces", 9.2000, new Vendor("vendor #4")));
                list.add(new Product(20, "Sir Rodney's Marmalade", "30 gift boxes", 81.0000, new Vendor("vendor #5")));
                list.add(new Product(21, "Sir Rodney's Scones", "24 pkgs. x 4 pieces", 10.0000, new Vendor("vendor #1")));
                list.add(new Product(22, "Gustaf's Knäckebröd", "24 - 500 g pkgs.", 21.0000, new Vendor("vendor #2")));
                list.add(new Product(23, "Tunnbröd", "12 - 250 g pkgs.", 9.0000, new Vendor("vendor #3")));
                list.add(new Product(24, "Guaraná Fantástica", "12 - 355 ml cans", 4.5000, new Vendor("vendor #4")));
                list.add(new Product(25, "NuNuCa Nuß-Nougat-Creme", "20 - 450 g glasses", 14.0000, new Vendor("vendor #5")));
                list.add(new Product(26, "Gumbär Gummibärchen", "100 - 250 g bags", 31.2300, new Vendor("vendor #1")));
                list.add(new Product(27, "Schoggi Schokolade", "100 - 100 g pieces", 43.9000, new Vendor("vendor #2")));
                list.add(new Product(28, "Rössle Sauerkraut", "25 - 825 g cans", 45.6000, new Vendor("vendor #3")));
                list.add(new Product(29, "Thüringer Rostbratwurst", "50 bags x 30 sausgs.", 123.7900, new Vendor("vendor #4")));
                list.add(new Product(30, "Nord-Ost Matjeshering", "10 - 200 g glasses", 25.8900, new Vendor("vendor #5")));
                list.add(new Product(31, "Gorgonzola Telino", "12 - 100 g pkgs", 12.5000, new Vendor("vendor #1")));
                list.add(new Product(32, "Mascarpone Fabioli", "24 - 200 g pkgs.", 32.0000, new Vendor("vendor #2")));
                list.add(new Product(33, "Geitost", "500 g", 2.5000, new Vendor("vendor #3")));
                list.add(new Product(34, "Sasquatch Ale", "24 - 12 oz bottles", 14.0000, new Vendor("vendor #4")));
                list.add(new Product(35, "Steeleye Stout", "24 - 12 oz bottles", 18.0000, new Vendor("vendor #5")));
                list.add(new Product(36, "Inlagd Sill", "24 - 250 g  jars", 19.0000, new Vendor("vendor #1")));
                list.add(new Product(37, "Gravad lax", "12 - 500 g pkgs.", 26.0000, new Vendor("vendor #2")));
                list.add(new Product(38, "Côte de Blaye", "12 - 75 cl bottles", 263.5000, new Vendor("vendor #3")));
                list.add(new Product(39, "Chartreuse verte", "750 cc per bottle", 18.0000, new Vendor("vendor #4")));
                list.add(new Product(40, "Boston Crab Meat", "24 - 4 oz tins", 18.4000, new Vendor("vendor #5")));
                list.add(new Product(41, "Jack's New England Clam Chowder", "12 - 12 oz cans", 9.6500, new Vendor("vendor #1")));
                list.add(new Product(42, "Singaporean Hokkien Fried Mee", "32 - 1 kg pkgs.", 14.0000, new Vendor("vendor #2")));
                list.add(new Product(43, "Ipoh Coffee", "16 - 500 g tins", 46.0000, new Vendor("vendor #3")));
                list.add(new Product(44, "Gula Malacca", "20 - 2 kg bags", 19.4500, new Vendor("vendor #4")));
                list.add(new Product(45, "Rogede sild", "1k pkg.", 9.5000, new Vendor("vendor #5")));
                list.add(new Product(46, "Spegesild", "4 - 450 g glasses", 12.0000, new Vendor("vendor #1")));
                list.add(new Product(47, "Zaanse koeken", "10 - 4 oz boxes", 9.5000, new Vendor("vendor #2")));
                list.add(new Product(48, "Chocolade", "10 pkgs.", 12.7500, new Vendor("vendor #3")));
                list.add(new Product(49, "Maxilaku", "24 - 50 g pkgs.", 20.0000, new Vendor("vendor #4")));
                list.add(new Product(50, "Valkoinen suklaa", "12 - 100 g bars", 16.2500, new Vendor("vendor #5")));
                list.add(new Product(51, "Manjimup Dried Apples", "50 - 300 g pkgs.", 53.0000, new Vendor("vendor #1")));
                list.add(new Product(52, "Filo Mix", "16 - 2 kg boxes", 7.0000, new Vendor("vendor #2")));
                list.add(new Product(53, "Perth Pasties", "48 pieces", 32.8000, new Vendor("vendor #3")));
                list.add(new Product(54, "Tourtière", "16 pies", 7.4500, new Vendor("vendor #4")));
                list.add(new Product(55, "Pâté chinois", "24 boxes x 2 pies", 24.0000, new Vendor("vendor #5")));
                list.add(new Product(56, "Gnocchi di nonna Alice", "24 - 250 g pkgs.", 38.0000, new Vendor("vendor #1")));
                list.add(new Product(57, "Ravioli Angelo", "24 - 250 g pkgs.", 19.5000, new Vendor("vendor #2")));
                list.add(new Product(58, "Escargots de Bourgogne", "24 pieces", 13.2500, new Vendor("vendor #3")));
                list.add(new Product(59, "Raclette Courdavault", "5 kg pkg.", 55.0000, new Vendor("vendor #4")));
                list.add(new Product(60, "Camembert Pierrot", "15 - 300 g rounds", 34.0000, new Vendor("vendor #5")));
                list.add(new Product(61, "Sirop d'érable", "24 - 500 ml bottles", 28.5000, new Vendor("vendor #1")));
                list.add(new Product(62, "Tarte au sucre", "48 pies", 49.3000, new Vendor("vendor #2")));
                list.add(new Product(63, "Vegie-spread", "15 - 625 g jars", 43.9000, new Vendor("vendor #3")));
                list.add(new Product(64, "Wimmers gute Semmelknödel", "20 bags x 4 pieces", 33.2500, new Vendor("vendor #4")));
                list.add(new Product(65, "Louisiana Fiery Hot Pepper Sauce", "32 - 8 oz bottles", 21.0500, new Vendor("vendor #5")));
                list.add(new Product(66, "Louisiana Hot Spiced Okra", "24 - 8 oz jars", 17.0000, new Vendor("vendor #1")));
                list.add(new Product(67, "Laughing Lumberjack Lager", "24 - 12 oz bottles", 14.0000, new Vendor("vendor #2")));
                list.add(new Product(68, "Scottish Longbreads", "10 boxes x 8 pieces", 12.5000, new Vendor("vendor #3")));
                list.add(new Product(69, "Gudbrandsdalsost", "10 kg pkg.", 36.0000, new Vendor("vendor #4")));
                list.add(new Product(70, "Outback Lager", "24 - 355 ml bottles", 15.0000, new Vendor("vendor #5")));
                list.add(new Product(71, "Flotemysost", "10 - 500 g pkgs.", 21.5000, new Vendor("vendor #1")));
                list.add(new Product(72, "Mozzarella di Giovanni", "24 - 200 g pkgs.", 34.8000, new Vendor("vendor #2")));
                list.add(new Product(73, "Röd Kaviar", "24 - 150 g jars", 15.0000, new Vendor("vendor #3")));
                list.add(new Product(74, "Longlife Tofu", "5 kg pkg.", 10.0000, new Vendor("vendor #4")));
                list.add(new Product(75, "Rhönbräu Klosterbier", "24 - 0.5 l bottles", 7.7500, new Vendor("vendor #5")));
                list.add(new Product(76, "Lakkalikööri", "500 ml", 18.0000, new Vendor("vendor #1")));
                list.add(new Product(77, "Original Frankfurter grüne Soße", "12 boxes", 13.0000, new Vendor("vendor #2")));
            }
            public ProductDataProvider()
            {
                super(list);
            }

            @Override
            protected List<Product> getData()
            {
                List<Product> list = super.getData();
                SortParam<String> param =  this.state.getSort();

                if (param != null)
                {
                    Collections.sort(list, new ProductComparator(param.getProperty(), param.isAscending()));
                }

                return list;
            }

            @Override
            public ISortState<String> getSortState()
            {
                return this.state;
            }

            static class ProductComparator implements Comparator<Product>, Serializable
            {
                private static final long serialVersionUID = 1L;

                private final String property;
                private final boolean ascending;

                public ProductComparator(String property, boolean ascending)
                {
                    this.property = property;
                    this.ascending = ascending;
                }

                @Override
                public int compare(Product p1, Product p2)
                {
                    Object o1 = PropertyResolver.getValue(this.property, p1);
                    Object o2 = PropertyResolver.getValue(this.property, p2);

                    if (o1 != null && o2 != null)
                    {
                        Comparable<Object> c1 = toComparable(o1);
                        Comparable<Object> c2 = toComparable(o2);

                        return c1.compareTo(c2) * (this.ascending ? 1 : -1);
                    }

                    return 0;
                }

                @SuppressWarnings("unchecked")
                private static Comparable<Object> toComparable(Object o)
                {
                    if (o instanceof Comparable<?>)
                    {
                        return (Comparable<Object>) o;
                    }

                    throw new WicketRuntimeException("Object should be a Comparable");
                }
            }
        }

    }

}


