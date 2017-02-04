package be.shad.tsqb.domain.usertype;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class TextWrappingObjectUserType implements UserType {

    private static final int SQL_TYPE = Types.VARCHAR;

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return new TextWrappingObject((String) cached);
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return ((TextWrappingObject) value).getText();
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;// imutable so just return original
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null || y == null) {
            return x == y;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        final String text = rs.getString(names[0]);
        if (rs.wasNull()) {
            return null;
        }
        return new TextWrappingObject(text);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, SQL_TYPE);
        } else {
            if (value instanceof TextWrappingObject) {
                TextWrappingObject obj = (TextWrappingObject) value;
                st.setObject(index, obj.getText(), SQL_TYPE);
            } else {
                throw new HibernateException("Expected a TextWrappingObject");
            }
        }
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public Class<?> returnedClass() {
        return TextWrappingObject.class;
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { SQL_TYPE };
    }
}
