package uu.toolbox.data;

import org.junit.Assert;

import uu.toolbox.core.UURandom;

@UUSqlTable(tableName = "uu_data_model_with_compound_key", existsInVersion = 2)
public class UUDataModelWithCompoundKey implements UUDataModel
{
    @UUSqlColumn(name = "c1", primaryKey = true)
    public String c1;

    @UUSqlColumn(name = "c2", primaryKey = true)
    public String c2;

    @UUSqlColumn(name = "data")
    public String data;

    public static UUDataModelWithCompoundKey random()
    {
        UUDataModelWithCompoundKey m = new UUDataModelWithCompoundKey();
        m.c1 = UURandom.randomLetters(20);
        m.c2 = UURandom.randomLetters(20);
        m.data = UURandom.randomLetters(20);
        return m;
    }

    public static void assertEquals(UUDataModelWithCompoundKey obj, UUDataModelWithCompoundKey other)
    {
        Assert.assertNotNull(obj);
        Assert.assertNotNull(other);
        Assert.assertEquals(obj.c1, other.c1);
        Assert.assertEquals(obj.c2, other.c2);
        Assert.assertEquals(obj.data, other.data);
    }
}
