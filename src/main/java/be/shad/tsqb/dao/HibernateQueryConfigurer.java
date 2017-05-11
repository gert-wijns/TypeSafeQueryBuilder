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
package be.shad.tsqb.dao;

import org.hibernate.Query;
import org.hibernate.Session;

public interface HibernateQueryConfigurer {

    /**
     * Configure the session before .list is called.
     */
    void beforeQuery(Session currentSession);

    /**
     * Configure the session after .list is called.
     */
    void afterQuery(Session currentSession);

    /**
     * Configure the query before .list is called.
     */
    void configureQuery(Query query);
    
    /**
     * 
     * @return true if a Session is passed along, 
     * false if the default getCurrentSession() method of the sessionFactory is to be used.
     */
    boolean hasSession();
    
    /**
     * The session that can be passed along to the dao
     * @return
     */
    Session getSession();

}
