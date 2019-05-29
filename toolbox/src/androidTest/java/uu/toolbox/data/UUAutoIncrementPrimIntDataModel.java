package uu.toolbox.data;

import org.junit.Assert;

import uu.toolbox.core.UURandom;

@UUSqlTable(existsInVersion = 4)
public class UUAutoIncrementPrimIntDataModel implements UUDataModel
{
    @UUSqlColumn(type = UUSqlColumn.Type.INTEGER_PRIMARY_KEY_AUTOINCREMENT)
    public int id;

    @UUSqlColumn()
    public String name;

    @UUSqlColumn()
    public int score;

    @UUSqlColumn()
    public long timestamp;

    public static UUAutoIncrementPrimIntDataModel random()
    {
        UUAutoIncrementPrimIntDataModel m = new UUAutoIncrementPrimIntDataModel();
        m.score = UURandom.randomInt();
        m.timestamp = System.currentTimeMillis();
        m.name = UURandom.randomLetters(20);
        m.id = 0;//UURandom.randomInt();
        return m;
    }

    public static void assertEquals(UUAutoIncrementPrimIntDataModel obj, UUAutoIncrementPrimIntDataModel other)
    {
        Assert.assertNotNull(obj);
        Assert.assertNotNull(other);
        Assert.assertEquals(obj.id, other.id);
        Assert.assertEquals(obj.name, other.name);
        Assert.assertEquals(obj.score, other.score);
        Assert.assertEquals(obj.timestamp, other.timestamp);
    }

}
