TypeSafeQueryBuilder
====================

The goal of this library is to make it possible for you to write queries 
without error-prone and hard to refactor string concatenations.

To achieve this goal, the <i>TypeSafeQueryBuilder</i> makes use of proxies. 
You simply use the methods on these proxies together with the query object to build a query. 
Doing this, you can make use of the standard content assist available in all good development tools. 
Besides the content assist, the compiler will also complain when non-compiling code is written, 
or when code was refactored and the query wasn't. If this doesn't make sense, don't worry, 
simply start reading the [getting started](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Getting-started) page and go from there.

These are the topics available on this wiki:

Main topics
* [Getting started](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Getting-started)
* [Joining entities](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Joining-entities)
* [Filtering](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Filtering-entities)
* [Selecting](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Selecting-the-relevant-data)
* [Grouping](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Grouping-data)
* [Sorting](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Sorting-data)

Additional topics
* [Functions](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Functions)
* [Joining: with](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Joining-With) (advanced)
* [Subqueries](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Subquerying)
* [What if I really really need custom hql?](https://github.com/gert-wijns/TypeSafeQueryBuilder/wiki/Custom-HQL)
