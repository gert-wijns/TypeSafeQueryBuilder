The goal of <i>`TypeSafeQueryBuilder`</i> is to allow writing queries using your hibernate configured entity classes rather than hand-coded HQL strings.

For an overview of the available functionality, see [Functionality overview](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Functionality-overview). The rest of this page will be a basic tutorial to get started. Basic knowledge of HQL is required to understand the examples and functionality overview.

#### Setup
Add library using maven:
```xml
<dependency>
    <groupId>com.github.gert-wijns</groupId>
    <artifactId>TypeSafeQueryBuilder</artifactId>
    <version>1.15.0</version>
</dependency>
```

To obtain a query, create a <i>TypeSafeQueryDao</i> with the hibernate sessionFactory and call the <i>`createQuery()`</i> method on it. 

```java
TypeSafeQueryDao dao = new TypeSafeQueryDaoImpl(sessionFactory);
TypeSafeRootQuery query = dao.createQuery();
```

To list the query results, call the <i>`doQuery(TypeSafeRootQuery query)`</i> method available on the <i>TypeSafeQueryDao</i>.

```java
// build useful query and then use doQuery to list the results:
List<InterestingData> results = dao.doQuery(query);
```

#### From clause
To query <i>from</i> an entity, use the <i>`query.from(Class<?> entityClass)`</i> method. This will return a proxy of the entityClass to continue building the query.

```java
Person person = query.from(Person.class); // would select people

=> "from Person hobj1" // Note: automatic unique alias provided
```

See also:
- [Defining multiple from clauses in a single query](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Defining-multiple-from-clauses-in-a-single-query)
- [Specifying a custom HQL alias](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Specifying-a-custom-HQL-alias)

#### Select clause
There is a wide range of select options for various cases. This example will deal with selecting a subset of entity properties into a dto.

To select data into a dto, create a dto proxy using the <i>`query.select(Class<?> dtoClass)`</i> method. This will return a proxy of the dtoClass to which data of the entity can be set using a setter of the dto proxy with a getter of the entity proxy. Note: calling the setter and getter will only bind the property to be selected into the dto. The value is only available after listing the query results.

```java
// select creates proxy instance of dto class
PersonDto personDto = query.select(PersonDto.class); 
// binds person age to the personAge property
personDto.setPersonAge(person.getAge()); 

=> "select hobj1.age as personAge from Person hobj1"
```
See also:
- [Selecting an object or object[] like with session.list()](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Selecting-an-object-or-object%5B%5D-like-with-session.list%28%29)
- [Selecting a second set of data to merge into the result](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Selecting-a-second-set-of-data-to-merge-into-the-result)
- [Selecting a collection into the dto](Selecting a collection into the dto)
- [Selecting a value of a different type with a transformer](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Selecting-a-value-of-a-different-type-with-a-transformer)
- [Selecting values into embedded dtos](Selecting values into embedded dtos)
- [Selecting values from embedded entities](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Selecting-values-from-embedded-entities)
- [Selecting arbitrary TypeSafeValues](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Selecting-TypeSafeValues)

#### Where clause
To specificy the where clause of a query, start by using the <i>`query.where(property)`</i> method. Depending on the property type, a list of relevant methods to build the restriction will be available. For example: using a Date as property, it is possible to check if the date is not before some other date while this check would not be available when using a Number property.

```java
query.where(person.getAge()).gt(50)

=> "from Person hobj1 where hobj1.age > :np1" [np1=50]
```

Adding more <i>`and`</i> restrictions and grouping <i>`or`</i> restrictions is covered in the functionality overview.

See also:
- [Chaining restrictions](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Chaining-restrictions)
- [Creating restriction groups](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Creating-restriction-groups)
- [Holding on to a restriction to query twice with altered restriction](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Holding-on-to-a-restriction-to-query-twice-with-altered-restriction)
- [Using restriction predicates to reduce code clutter](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Restriction-predicates)

#### Join clause
Entities can be joined by using the <i>`query.join(...)`</i> methods.
Additionally, entities may also be joined implicitely by using the getter method of an entity proxy.
It is only possible to obtain a proxy of a collection relation by using the <i>`join(...)`</i> methods.

```java
// join and obtain a proxy of a collection element
Relation childRelation = query.join(parent.getChildRelations()); 
// join implicitly, returns a proxy of the getter type
Person child = childRelation.getChild(); 

=> "from Person hobj1 join hobj1.childRelations hobj2 join hobj2.child hobj3"
````
See also:
- [TypeSafeQueryBuilder JoinTypes](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/TypeSafeQueryBuilder-JoinTypes)
- [HQL joining with](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/HQL-joining-with)

#### Order By clause
Sorting values can be done by using the <i>`query.orderBy()`</i> method and subsequently calling the <i>`desc(...)`</i> or <i>`asc(...)`</i> methods. These methods can be chained to sort by multiple values.

```java
query.orderBy().desc(person.getName()).
                 asc(person.getAge());

=> "from Person hobj1 order by hobj1.name desc, hobj1.age"
```
See also:
- [Ordering by a selection result dto field (functional sorting)](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Ordering-by-a-selection-result-dto-field-%28functional-sorting%29)

#### Group By clause
Grouping values can be done by using the <i>`query.groupBy(...)`<i> method.

```java
PersonDto personDto = query.select(PersonDto.class);
personDto.setPersonAge(person.getAge());
query.groupBy(person.getAge());

=> "select hobj1.age as personAge from Person hobj1 group by hobj1.age"
```

See also:
- [Using the result of a group by to select](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Using-the-result-of-a-group-by-to-select)

#### Having clause
Having restrictions can be add by using <i>`query.having(...)`</i>.

```java
Building building = query.from(Building.class);
query.select(building.getConstructionDate());
query.groupBy(building.getConstructionDate());

Date dateArg = new Date();
query.having(building.getConstructionDate()).after(dateArg);

=> "select hobj1.constructionDate 
    from Building hobj1 
    group by hobj1.constructionDate 
    having hobj1.constructionDate > :np1"
params [np1=dateArg]
```