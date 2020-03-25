package in.bloomington.incident.model;

/**
 * general use class useful for auto complete owners, addresses, etc
 *
 */
public class Item implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    private int    id;
    private String name;

    //
    public Item()
    {
    }

    public Item(int id, String name)
    {
        this.id   = id;
        this.name = name;
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        int ret = 29;
        if (id > 0) ret += 37 * id;
        return ret;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Item)) {
            return false;
        }
        Item item = (Item) o;
        return (item.getId() == id);
    }		

}
