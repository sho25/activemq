begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|plugin
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|spring
operator|.
name|Utils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|FactoryBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
specifier|public
class|class
name|PropertiesPlaceHolderUtil
block|{
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PropertiesPlaceHolderUtil
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\$\\{([^\\}]+)\\}"
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|properties
decl_stmt|;
specifier|public
name|PropertiesPlaceHolderUtil
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
specifier|public
name|void
name|filter
parameter_list|(
name|Properties
name|toFilter
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|toFilter
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|val
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|newVal
init|=
name|filter
argument_list|(
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|val
operator|.
name|equals
argument_list|(
name|newVal
argument_list|)
condition|)
block|{
name|toFilter
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|newVal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|String
name|filter
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|int
name|start
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|matcher
operator|.
name|find
argument_list|(
name|start
argument_list|)
condition|)
block|{
break|break;
block|}
name|String
name|group
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|property
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|str
operator|=
name|matcher
operator|.
name|replaceFirst
argument_list|(
name|Matcher
operator|.
name|quoteReplacement
argument_list|(
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|start
operator|=
name|matcher
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|replaceBytePostfix
argument_list|(
name|str
argument_list|)
return|;
block|}
specifier|static
name|Pattern
index|[]
name|byteMatchers
init|=
operator|new
name|Pattern
index|[]
block|{
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*(\\d+)\\s*(b)?\\s*$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
block|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*(\\d+)\\s*k(b)?\\s*$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
block|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*(\\d+)\\s*m(b)?\\s*$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
block|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*(\\d+)\\s*g(b)?\\s*$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
block|}
decl_stmt|;
comment|// xbean can Xb, Xkb, Xmb, Xg etc
specifier|private
name|String
name|replaceBytePostfix
parameter_list|(
name|String
name|str
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|byteMatchers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Matcher
name|matcher
init|=
name|byteMatchers
index|[
name|i
index|]
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|long
name|value
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|i
condition|;
name|j
operator|++
control|)
block|{
name|value
operator|*=
literal|1024
expr_stmt|;
block|}
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ignored
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"nfe on: "
operator|+
name|str
argument_list|,
name|ignored
argument_list|)
expr_stmt|;
block|}
return|return
name|str
return|;
block|}
specifier|public
name|void
name|mergeProperties
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Properties
name|initialProperties
parameter_list|,
name|BrokerContext
name|brokerContext
parameter_list|)
block|{
comment|// find resources
comment|//<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
comment|//<property name="locations" || name="properties">
comment|//              ...
comment|//</property>
comment|//</bean>
name|LinkedList
argument_list|<
name|String
argument_list|>
name|resources
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|LinkedList
argument_list|<
name|String
argument_list|>
name|propertiesClazzes
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|NodeList
name|beans
init|=
name|doc
operator|.
name|getElementsByTagNameNS
argument_list|(
literal|"*"
argument_list|,
literal|"bean"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|beans
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|bean
init|=
name|beans
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|bean
operator|.
name|hasAttributes
argument_list|()
operator|&&
name|bean
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
literal|"class"
argument_list|)
operator|.
name|getTextContent
argument_list|()
operator|.
name|contains
argument_list|(
literal|"PropertyPlaceholderConfigurer"
argument_list|)
condition|)
block|{
if|if
condition|(
name|bean
operator|.
name|hasChildNodes
argument_list|()
condition|)
block|{
name|NodeList
name|beanProps
init|=
name|bean
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|beanProps
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|Node
name|beanProp
init|=
name|beanProps
operator|.
name|item
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|Node
operator|.
name|ELEMENT_NODE
operator|==
name|beanProp
operator|.
name|getNodeType
argument_list|()
operator|&&
name|beanProp
operator|.
name|hasAttributes
argument_list|()
operator|&&
name|beanProp
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
literal|"name"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|String
name|propertyName
init|=
name|beanProp
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
literal|"name"
argument_list|)
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"locations"
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
comment|// interested in value or list/value of locations property
name|Element
name|beanPropElement
init|=
operator|(
name|Element
operator|)
name|beanProp
decl_stmt|;
name|NodeList
name|values
init|=
name|beanPropElement
operator|.
name|getElementsByTagNameNS
argument_list|(
literal|"*"
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|values
operator|.
name|getLength
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|Node
name|value
init|=
name|values
operator|.
name|item
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|value
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"properties"
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
comment|// bean or beanFactory
name|Element
name|beanPropElement
init|=
operator|(
name|Element
operator|)
name|beanProp
decl_stmt|;
name|NodeList
name|values
init|=
name|beanPropElement
operator|.
name|getElementsByTagNameNS
argument_list|(
literal|"*"
argument_list|,
literal|"bean"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|values
operator|.
name|getLength
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|Node
name|value
init|=
name|values
operator|.
name|item
argument_list|(
name|k
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|hasAttributes
argument_list|()
condition|)
block|{
name|Node
name|beanClassTypeNode
init|=
name|value
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
literal|"class"
argument_list|)
decl_stmt|;
if|if
condition|(
name|beanClassTypeNode
operator|!=
literal|null
condition|)
block|{
name|propertiesClazzes
operator|.
name|add
argument_list|(
name|beanClassTypeNode
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
block|}
block|}
for|for
control|(
name|String
name|value
range|:
name|propertiesClazzes
control|)
block|{
try|try
block|{
name|Object
name|springBean
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|value
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|springBean
operator|instanceof
name|FactoryBean
condition|)
block|{
comment|// can't access the factory or created properties from spring context so we got to recreate
name|initialProperties
operator|.
name|putAll
argument_list|(
operator|(
name|Properties
operator|)
name|FactoryBean
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"getObject"
argument_list|,
operator|(
name|Class
argument_list|<
name|?
argument_list|>
index|[]
operator|)
literal|null
argument_list|)
operator|.
name|invoke
argument_list|(
name|springBean
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"unexpected exception processing properties bean class: "
operator|+
name|propertiesClazzes
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Resource
argument_list|>
name|propResources
init|=
operator|new
name|LinkedList
argument_list|<
name|Resource
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|resources
control|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|value
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|propResources
operator|.
name|add
argument_list|(
name|Utils
operator|.
name|resourceFromString
argument_list|(
name|filter
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"failed to resolve resource: "
operator|+
name|value
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Resource
name|resource
range|:
name|propResources
control|)
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|properties
operator|.
name|load
argument_list|(
name|resource
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"failed to load properties resource: "
operator|+
name|resource
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|initialProperties
operator|.
name|putAll
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
