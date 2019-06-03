package uu.toolbox.data;

import org.junit.Assert;

import uu.toolbox.core.UURandom;

@UUSqlTable(tableName = "all_columns", existsInVersion = 4)
public class UUAllColumnTypesDataModel implements UUDataModel
{
    @UUSqlColumn()
    public Byte byteObject;

    @UUSqlColumn()
    public byte bytePrimitive;

    @UUSqlColumn()
    public Short shortObject;

    @UUSqlColumn()
    public short shortPrimitive;

    @UUSqlColumn()
    public Integer intObject;

    @UUSqlColumn()
    public int intPrimitive;

    @UUSqlColumn()
    public Long longObject;

    @UUSqlColumn()
    public long longPrimitive;

    @UUSqlColumn()
    public Double doubleObject;

    @UUSqlColumn()
    public double doublePrimitive;

    @UUSqlColumn()
    public Float floatObject;

    @UUSqlColumn()
    public float floatPrimitive;

    @UUSqlColumn()
    public Boolean booleanObject;

    @UUSqlColumn()
    public boolean booleanPrimitive;

    @UUSqlColumn()
    public Character characterObject;

    @UUSqlColumn()
    public char characterPrimitive;

    @UUSqlColumn()
    public Byte[] byteArrayObject;

    @UUSqlColumn()
    public byte[] byteArrayPrimitive;

    @UUSqlColumn()
    public String stringObject;

    public enum SomeEnum
    {
        Apple,
        Banana,
        Carrot
    }

    @UUSqlColumn()
    private SomeEnum singleEnum;



    public static UUAllColumnTypesDataModel random()
    {
        UUAllColumnTypesDataModel o = new UUAllColumnTypesDataModel();
        o.byteObject = UURandom.randomByte();
        o.bytePrimitive = UURandom.randomByte();
        o.shortObject = UURandom.randomShort();
        o.shortPrimitive = UURandom.randomShort();
        o.intObject = UURandom.randomInt();
        o.intPrimitive = UURandom.randomInt();
        o.longObject = UURandom.randomLong();
        o.longPrimitive = UURandom.randomLong();
        o.doubleObject = UURandom.randomDouble();
        o.doublePrimitive = UURandom.randomDouble();
        o.floatObject = UURandom.randomFloat();
        o.floatPrimitive = UURandom.randomFloat();
        o.booleanObject = UURandom.randomBoolean();
        o.booleanPrimitive = UURandom.randomBoolean();
        o.characterObject = (char) UURandom.randomByte();
        o.characterPrimitive = (char)UURandom.randomByte();
        o.byteArrayObject = UURandom.randomByteObjArray(50);
        o.byteArrayPrimitive = UURandom.randomByteArray(50);
        o.stringObject = UURandom.randomString(50);
        o.singleEnum = SomeEnum.values()[UURandom.randomInt(SomeEnum.values().length)];
        return o;
    }

    public static void assertEquals(UUAllColumnTypesDataModel obj, UUAllColumnTypesDataModel other)
    {
        Assert.assertNotNull(obj);
        Assert.assertNotNull(other);
        Assert.assertEquals(obj.byteObject, other.byteObject);
        Assert.assertEquals(obj.bytePrimitive, other.bytePrimitive);
        Assert.assertEquals(obj.shortObject, other.shortObject);
        Assert.assertEquals(obj.shortPrimitive, other.shortPrimitive);
        Assert.assertEquals(obj.intObject, other.intObject);
        Assert.assertEquals(obj.intPrimitive, other.intPrimitive);
        Assert.assertEquals(obj.longObject, other.longObject);
        Assert.assertEquals(obj.longPrimitive, other.longPrimitive);
        Assert.assertEquals(obj.doubleObject, other.doubleObject);
        Assert.assertEquals(obj.doublePrimitive, other.doublePrimitive, 0);
        Assert.assertEquals(obj.floatObject, other.floatObject);
        Assert.assertEquals(obj.floatPrimitive, other.floatPrimitive, 0);
        Assert.assertEquals(obj.booleanObject, other.booleanObject);
        Assert.assertEquals(obj.booleanPrimitive, other.booleanPrimitive);
        Assert.assertEquals(obj.characterObject, other.characterObject);
        Assert.assertEquals(obj.characterPrimitive, other.characterPrimitive);
        Assert.assertArrayEquals(obj.byteArrayObject, other.byteArrayObject);
        Assert.assertArrayEquals(obj.byteArrayPrimitive, other.byteArrayPrimitive);
        Assert.assertEquals(obj.stringObject, other.stringObject);
        Assert.assertEquals(obj.singleEnum, other.singleEnum);
    }
}
