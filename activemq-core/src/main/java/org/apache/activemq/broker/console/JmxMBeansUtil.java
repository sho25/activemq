begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|console
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
name|console
operator|.
name|filter
operator|.
name|QueryFilter
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
name|broker
operator|.
name|console
operator|.
name|filter
operator|.
name|MBeansObjectNameQueryFilter
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
name|broker
operator|.
name|console
operator|.
name|filter
operator|.
name|WildcardToRegExTransformFilter
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
name|broker
operator|.
name|console
operator|.
name|filter
operator|.
name|MBeansRegExQueryFilter
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
name|broker
operator|.
name|console
operator|.
name|filter
operator|.
name|MBeansAttributeQueryFilter
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
name|broker
operator|.
name|console
operator|.
name|filter
operator|.
name|PropertiesViewFilter
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
name|broker
operator|.
name|console
operator|.
name|filter
operator|.
name|StubQueryFilter
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
name|broker
operator|.
name|console
operator|.
name|filter
operator|.
name|MapTransformFilter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXServiceURL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Iterator
import|;
end_import

begin_class
specifier|public
class|class
name|JmxMBeansUtil
block|{
specifier|public
specifier|static
name|List
name|getAllBrokers
parameter_list|(
name|JMXServiceURL
name|jmxUrl
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
operator|new
name|MBeansObjectNameQueryFilter
argument_list|(
name|jmxUrl
argument_list|)
operator|)
operator|.
name|query
argument_list|(
literal|"Type=Broker"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
name|getBrokersByName
parameter_list|(
name|JMXServiceURL
name|jmxUrl
parameter_list|,
name|String
name|brokerName
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
operator|new
name|MBeansObjectNameQueryFilter
argument_list|(
name|jmxUrl
argument_list|)
operator|)
operator|.
name|query
argument_list|(
literal|"Type=Broker,BrokerName="
operator|+
name|brokerName
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
name|getAllBrokers
parameter_list|(
name|JMXServiceURL
name|jmxUrl
parameter_list|,
name|Set
name|attributes
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
operator|new
name|MBeansAttributeQueryFilter
argument_list|(
name|jmxUrl
argument_list|,
name|attributes
argument_list|,
operator|new
name|MBeansObjectNameQueryFilter
argument_list|(
name|jmxUrl
argument_list|)
argument_list|)
operator|)
operator|.
name|query
argument_list|(
literal|"Type=Broker"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
name|getBrokersByName
parameter_list|(
name|JMXServiceURL
name|jmxUrl
parameter_list|,
name|String
name|brokerName
parameter_list|,
name|Set
name|attributes
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
operator|new
name|MBeansAttributeQueryFilter
argument_list|(
name|jmxUrl
argument_list|,
name|attributes
argument_list|,
operator|new
name|MBeansObjectNameQueryFilter
argument_list|(
name|jmxUrl
argument_list|)
argument_list|)
operator|)
operator|.
name|query
argument_list|(
literal|"Type=Broker,BrokerName="
operator|+
name|brokerName
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
name|queryMBeans
parameter_list|(
name|JMXServiceURL
name|jmxUrl
parameter_list|,
name|List
name|queryList
parameter_list|)
throws|throws
name|Exception
block|{
comment|// If there is no query defined get all mbeans
if|if
condition|(
name|queryList
operator|==
literal|null
operator|||
name|queryList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|createMBeansObjectNameQuery
argument_list|(
name|jmxUrl
argument_list|)
operator|.
name|query
argument_list|(
literal|""
argument_list|)
return|;
comment|// Parse through all the query strings
block|}
else|else
block|{
return|return
name|createMBeansObjectNameQuery
argument_list|(
name|jmxUrl
argument_list|)
operator|.
name|query
argument_list|(
name|queryList
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|List
name|queryMBeans
parameter_list|(
name|JMXServiceURL
name|jmxUrl
parameter_list|,
name|List
name|queryList
parameter_list|,
name|Set
name|attributes
parameter_list|)
throws|throws
name|Exception
block|{
comment|// If there is no query defined get all mbeans
if|if
condition|(
name|queryList
operator|==
literal|null
operator|||
name|queryList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|createMBeansAttributeQuery
argument_list|(
name|jmxUrl
argument_list|,
name|attributes
argument_list|)
operator|.
name|query
argument_list|(
literal|""
argument_list|)
return|;
comment|// Parse through all the query strings
block|}
else|else
block|{
return|return
name|createMBeansAttributeQuery
argument_list|(
name|jmxUrl
argument_list|,
name|attributes
argument_list|)
operator|.
name|query
argument_list|(
name|queryList
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|List
name|queryMBeans
parameter_list|(
name|JMXServiceURL
name|jmxUrl
parameter_list|,
name|String
name|queryString
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createMBeansObjectNameQuery
argument_list|(
name|jmxUrl
argument_list|)
operator|.
name|query
argument_list|(
name|queryString
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
name|queryMBeans
parameter_list|(
name|JMXServiceURL
name|jmxUrl
parameter_list|,
name|String
name|queryString
parameter_list|,
name|Set
name|attributes
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createMBeansAttributeQuery
argument_list|(
name|jmxUrl
argument_list|,
name|attributes
argument_list|)
operator|.
name|query
argument_list|(
name|queryString
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
name|filterMBeansView
parameter_list|(
name|List
name|mbeans
parameter_list|,
name|Set
name|viewFilter
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
operator|new
name|PropertiesViewFilter
argument_list|(
name|viewFilter
argument_list|,
operator|new
name|MapTransformFilter
argument_list|(
operator|new
name|StubQueryFilter
argument_list|(
name|mbeans
argument_list|)
argument_list|)
argument_list|)
operator|.
name|query
argument_list|(
literal|""
argument_list|)
operator|)
return|;
block|}
specifier|public
specifier|static
name|String
name|createQueryString
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|param
parameter_list|)
block|{
return|return
name|query
operator|.
name|replaceAll
argument_list|(
literal|"%1"
argument_list|,
name|param
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|createQueryString
parameter_list|(
name|String
name|query
parameter_list|,
name|List
name|params
parameter_list|)
block|{
name|int
name|count
init|=
literal|1
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|params
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|query
operator|.
name|replaceAll
argument_list|(
literal|"%"
operator|+
name|count
operator|++
argument_list|,
name|i
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
specifier|public
specifier|static
name|QueryFilter
name|createMBeansObjectNameQuery
parameter_list|(
name|JMXServiceURL
name|jmxUrl
parameter_list|)
block|{
return|return
operator|new
name|WildcardToRegExTransformFilter
argument_list|(
comment|// Let us be able to accept wildcard queries
operator|new
name|MBeansRegExQueryFilter
argument_list|(
comment|// Use regular expressions to filter the query results
operator|new
name|MBeansObjectNameQueryFilter
argument_list|(
name|jmxUrl
argument_list|)
comment|// Let us retrieve the mbeans object name specified by the query
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|QueryFilter
name|createMBeansAttributeQuery
parameter_list|(
name|JMXServiceURL
name|jmxUrl
parameter_list|,
name|Set
name|attributes
parameter_list|)
block|{
return|return
operator|new
name|WildcardToRegExTransformFilter
argument_list|(
comment|// Let use be able to accept wildcard queries
operator|new
name|MBeansRegExQueryFilter
argument_list|(
comment|// Use regular expressions to filter the query result
operator|new
name|MBeansAttributeQueryFilter
argument_list|(
name|jmxUrl
argument_list|,
name|attributes
argument_list|,
comment|// Retrieve the attributes needed
operator|new
name|MBeansObjectNameQueryFilter
argument_list|(
name|jmxUrl
argument_list|)
comment|// Retrieve the mbeans object name specified by the query
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

