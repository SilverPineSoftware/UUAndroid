package uu.toolbox.data;

@UUSqlTable(tableName = "uu_complex_test_model", existsInVersion = 3)
public class UUComplexDataModel implements UUDataModel
{
    @UUSqlColumn(name = "id", type = UUSqlColumn.Type.INTEGER_PRIMARY_KEY_AUTOINCREMENT)
    private int id;

    @UUSqlColumn(name = "string_nullable_nodefault", type = UUSqlColumn.Type.TEXT, nullable = true)
    private String stringNullableNoDefault;

    @UUSqlColumn(name = "string_nullable_withdefault", type = UUSqlColumn.Type.TEXT, nullable = true, defaultValue = "Foo")
    private String stringNullableWithDefault;

    @UUSqlColumn(name = "string_nonnull_nodefault", type = UUSqlColumn.Type.TEXT, nonNull = true)
    private String stringNonNullNoDefault;

    @UUSqlColumn(name = "string_nonnull_withdefault", type = UUSqlColumn.Type.TEXT, nonNull = true, defaultValue = "Bar")
    private String stringNonNullWithDefault;

    @UUSqlColumn(name = "int_nullable_nodefault", type = UUSqlColumn.Type.INTEGER, nullable = true)
    private int intNullableNoDefault;

    @UUSqlColumn(name = "int_nullable_withdefault", type = UUSqlColumn.Type.INTEGER, nullable = true, defaultValue = "99")
    private int intNullableWithDefault;

    @UUSqlColumn(name = "int_nonnull_nodefault", type = UUSqlColumn.Type.INTEGER, nonNull = true)
    private int intNonNullNoDefault;

    @UUSqlColumn(name = "int_nonnull_withdefault", type = UUSqlColumn.Type.INTEGER, nonNull = true, defaultValue = "57.0")
    private int intNonNullWithDefault;

    @UUSqlColumn(name = "real_nullable_nodefault", type = UUSqlColumn.Type.REAL, nullable = true)
    private double realNullableNoDefault;

    @UUSqlColumn(name = "real_nullable_withdefault", type = UUSqlColumn.Type.REAL, nullable = true, defaultValue = "32.0")
    private double realNullableWithDefault;

    @UUSqlColumn(name = "real_nonnull_nodefault", type = UUSqlColumn.Type.REAL, nonNull = true)
    private double realNonNullNoDefault;

    @UUSqlColumn(name = "real_nonnull_withdefault", type = UUSqlColumn.Type.REAL, nonNull = true, defaultValue = "4")
    private double realNonNullWithDefault;

    @UUSqlColumn(name = "blob_nullable_nodefault", type = UUSqlColumn.Type.BLOB, nullable = true)
    private byte[] blobNullableNoDefault;

    @UUSqlColumn(name = "blob_nullable_withdefault", type = UUSqlColumn.Type.BLOB, nullable = true, defaultValue = "0xABCD")
    private byte[] blobNullableWithDefault;

    @UUSqlColumn(name = "blob_nonnull_nodefault", type = UUSqlColumn.Type.BLOB, nonNull = true)
    private byte[] blobNonNullNoDefault;

    @UUSqlColumn(name = "blob_nonnull_withdefault", type = UUSqlColumn.Type.BLOB, nonNull = true, defaultValue = "0x1234")
    private byte[] blobNonNullWithDefault;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getStringNullableNoDefault()
    {
        return stringNullableNoDefault;
    }

    public void setStringNullableNoDefault(String stringNullableNoDefault)
    {
        this.stringNullableNoDefault = stringNullableNoDefault;
    }

    public String getStringNullableWithDefault()
    {
        return stringNullableWithDefault;
    }

    public void setStringNullableWithDefault(String stringNullableWithDefault)
    {
        this.stringNullableWithDefault = stringNullableWithDefault;
    }

    public String getStringNonNullNoDefault()
    {
        return stringNonNullNoDefault;
    }

    public void setStringNonNullNoDefault(String stringNonNullNoDefault)
    {
        this.stringNonNullNoDefault = stringNonNullNoDefault;
    }

    public String getStringNonNullWithDefault()
    {
        return stringNonNullWithDefault;
    }

    public void setStringNonNullWithDefault(String stringNonNullWithDefault)
    {
        this.stringNonNullWithDefault = stringNonNullWithDefault;
    }

    public int getIntNullableNoDefault()
    {
        return intNullableNoDefault;
    }

    public void setIntNullableNoDefault(int intNullableNoDefault)
    {
        this.intNullableNoDefault = intNullableNoDefault;
    }

    public int getIntNullableWithDefault()
    {
        return intNullableWithDefault;
    }

    public void setIntNullableWithDefault(int intNullableWithDefault)
    {
        this.intNullableWithDefault = intNullableWithDefault;
    }

    public int getIntNonNullNoDefault()
    {
        return intNonNullNoDefault;
    }

    public void setIntNonNullNoDefault(int intNonNullNoDefault)
    {
        this.intNonNullNoDefault = intNonNullNoDefault;
    }

    public int getIntNonNullWithDefault()
    {
        return intNonNullWithDefault;
    }

    public void setIntNonNullWithDefault(int intNonNullWithDefault)
    {
        this.intNonNullWithDefault = intNonNullWithDefault;
    }

    public double getRealNullableNoDefault()
    {
        return realNullableNoDefault;
    }

    public void setRealNullableNoDefault(double realNullableNoDefault)
    {
        this.realNullableNoDefault = realNullableNoDefault;
    }

    public double getRealNullableWithDefault()
    {
        return realNullableWithDefault;
    }

    public void setRealNullableWithDefault(double realNullableWithDefault)
    {
        this.realNullableWithDefault = realNullableWithDefault;
    }

    public double getRealNonNullNoDefault()
    {
        return realNonNullNoDefault;
    }

    public void setRealNonNullNoDefault(double realNonNullNoDefault)
    {
        this.realNonNullNoDefault = realNonNullNoDefault;
    }

    public double getRealNonNullWithDefault()
    {
        return realNonNullWithDefault;
    }

    public void setRealNonNullWithDefault(double realNonNullWithDefault)
    {
        this.realNonNullWithDefault = realNonNullWithDefault;
    }

    public byte[] getBlobNullableNoDefault()
    {
        return blobNullableNoDefault;
    }

    public void setBlobNullableNoDefault(byte[] blobNullableNoDefault)
    {
        this.blobNullableNoDefault = blobNullableNoDefault;
    }

    public byte[] getBlobNullableWithDefault()
    {
        return blobNullableWithDefault;
    }

    public void setBlobNullableWithDefault(byte[] blobNullableWithDefault)
    {
        this.blobNullableWithDefault = blobNullableWithDefault;
    }

    public byte[] getBlobNonNullNoDefault()
    {
        return blobNonNullNoDefault;
    }

    public void setBlobNonNullNoDefault(byte[] blobNonNullNoDefault)
    {
        this.blobNonNullNoDefault = blobNonNullNoDefault;
    }

    public byte[] getBlobNonNullWithDefault()
    {
        return blobNonNullWithDefault;
    }

    public void setBlobNonNullWithDefault(byte[] blobNonNullWithDefault)
    {
        this.blobNonNullWithDefault = blobNonNullWithDefault;
    }
}
