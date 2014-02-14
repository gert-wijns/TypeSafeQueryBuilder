/*
 * Copyright Gert Wijns gert.wijns@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.shad.tsqb.domain.usertype;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

public class AddressType implements CompositeUserType {

    @Override
    public String[] getPropertyNames() {
        return new String[] { "street", "number" };
    }

    @Override
    public Type[] getPropertyTypes() {
        return new Type[] { StringType.INSTANCE, StringType.INSTANCE };
    }

    @Override
    public Object getPropertyValue(final Object component, final int property) throws HibernateException {
        final Address address = (Address) component;
        switch( property ) {
            case 0: return address.getStreet();
            case 1: return address.getNumber();
            default: throw new IllegalArgumentException(""+property);
        }
    }

    @Override
    public void setPropertyValue(final Object component, final int property, final Object setValue) throws HibernateException {
        final Address address = (Address) component;
        switch( property ) {
            case 0: address.setStreet((String) setValue); break;
            case 1: address.setNumber((String) setValue); break;
            default: throw new IllegalArgumentException(""+property);
        }
    }

    @Override
    public Object nullSafeGet(final ResultSet resultSet, final String[] names, final SessionImplementor paramSessionImplementor, 
            final Object paramObject) throws HibernateException, SQLException {
        Address address = null;
        final String street = resultSet.getString(names[0]);
        if (!resultSet.wasNull()) {
            address = new Address();
            address.setStreet(street);
            address.setNumber(resultSet.getString(names[1]));
        }
        return address;
    }

    @Override
    public void nullSafeSet(final PreparedStatement preparedStatement, final Object value, final int property, 
            final SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        if (null == value) {
            preparedStatement.setNull(property, StringType.INSTANCE.sqlType());
            preparedStatement.setNull(property + 1, StringType.INSTANCE.sqlType());
        } else {
            final Address address = (Address) value;
            preparedStatement.setString(property, address.getStreet());
            preparedStatement.setString(property + 1, address.getNumber());
        }
    }

    @Override
    public Serializable disassemble(final Object value, final SessionImplementor paramSessionImplementor) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(final Serializable cached, final SessionImplementor sessionImplementor, 
            final Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(final Object original, final Object target, final SessionImplementor paramSessionImplementor, 
            final Object owner) throws HibernateException {
        return this.deepCopy(original);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class returnedClass() {
        return Address.class;
    }

    @Override
    public boolean equals(final Object o1, final Object o2) throws HibernateException {
        boolean isEqual = false;
        if (o1 == o2) {
            isEqual = false;
        }
        if (null == o1 || null == o2) {
            isEqual = false;
        } else {
            isEqual = o1.equals(o2);
        }
        return isEqual;
    }

    @Override
    public int hashCode(final Object value) throws HibernateException {
        return value.hashCode();
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        if( value == null ) {
            return null;
        }
        final Address addressToCopy = (Address) value;
        final Address address = new Address();
        address.setStreet(addressToCopy.getStreet());
        address.setNumber(addressToCopy.getNumber());
        return address;
    }

}