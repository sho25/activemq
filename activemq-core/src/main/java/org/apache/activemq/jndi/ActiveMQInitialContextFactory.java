begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jndi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|spi
operator|.
name|InitialContextFactory
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
name|ActiveMQConnectionFactory
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
name|Broker
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
name|command
operator|.
name|ActiveMQQueue
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
name|command
operator|.
name|ActiveMQTopic
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * A factory of the ActiveMQ InitialContext which contains {@link ConnectionFactory}  * instances as well as a child context called<i>destinations</i> which contain all of the  * current active destinations, in child context depending on the QoS such as  * transient or durable and queue or topic.  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQInitialContextFactory
implements|implements
name|InitialContextFactory
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|defaultConnectionFactoryNames
init|=
block|{
literal|"ConnectionFactory"
block|,
literal|"QueueConnectionFactory"
block|,
literal|"TopicConnectionFactory"
block|}
decl_stmt|;
specifier|private
name|String
name|connectionPrefix
init|=
literal|"connection."
decl_stmt|;
specifier|private
name|String
name|queuePrefix
init|=
literal|"queue."
decl_stmt|;
specifier|private
name|String
name|topicPrefix
init|=
literal|"topic."
decl_stmt|;
specifier|public
name|Context
name|getInitialContext
parameter_list|(
name|Hashtable
name|environment
parameter_list|)
throws|throws
name|NamingException
block|{
comment|// lets create a factory
name|Map
name|data
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
name|String
index|[]
name|names
init|=
name|getConnectionFactoryNames
argument_list|(
name|environment
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
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
literal|null
decl_stmt|;
name|String
name|name
init|=
name|names
index|[
name|i
index|]
decl_stmt|;
try|try
block|{
name|factory
operator|=
name|createConnectionFactory
argument_list|(
name|name
argument_list|,
name|environment
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NamingException
argument_list|(
literal|"Invalid broker URL"
argument_list|)
throw|;
block|}
comment|/*     if( broker==null ) {                 try {                     broker = factory.getEmbeddedBroker();                 }                 catch (JMSException e) {                     log.warn("Failed to get embedded broker", e);                 }             }        */
name|data
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|factory
argument_list|)
expr_stmt|;
block|}
name|createQueues
argument_list|(
name|data
argument_list|,
name|environment
argument_list|)
expr_stmt|;
name|createTopics
argument_list|(
name|data
argument_list|,
name|environment
argument_list|)
expr_stmt|;
comment|/*         if (broker != null) {             data.put("destinations", broker.getDestinationContext(environment));         }         */
name|data
operator|.
name|put
argument_list|(
literal|"dynamicQueues"
argument_list|,
operator|new
name|LazyCreateContext
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6503881346214855588L
decl_stmt|;
specifier|protected
name|Object
name|createEntry
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|data
operator|.
name|put
argument_list|(
literal|"dynamicTopics"
argument_list|,
operator|new
name|LazyCreateContext
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2019166796234979615L
decl_stmt|;
specifier|protected
name|Object
name|createEntry
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|createContext
argument_list|(
name|environment
argument_list|,
name|data
argument_list|)
return|;
block|}
comment|// Properties
comment|//-------------------------------------------------------------------------
specifier|public
name|String
name|getTopicPrefix
parameter_list|()
block|{
return|return
name|topicPrefix
return|;
block|}
specifier|public
name|void
name|setTopicPrefix
parameter_list|(
name|String
name|topicPrefix
parameter_list|)
block|{
name|this
operator|.
name|topicPrefix
operator|=
name|topicPrefix
expr_stmt|;
block|}
specifier|public
name|String
name|getQueuePrefix
parameter_list|()
block|{
return|return
name|queuePrefix
return|;
block|}
specifier|public
name|void
name|setQueuePrefix
parameter_list|(
name|String
name|queuePrefix
parameter_list|)
block|{
name|this
operator|.
name|queuePrefix
operator|=
name|queuePrefix
expr_stmt|;
block|}
comment|// Implementation methods
comment|//-------------------------------------------------------------------------
specifier|protected
name|ReadOnlyContext
name|createContext
parameter_list|(
name|Hashtable
name|environment
parameter_list|,
name|Map
name|data
parameter_list|)
block|{
return|return
operator|new
name|ReadOnlyContext
argument_list|(
name|environment
argument_list|,
name|data
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|(
name|String
name|name
parameter_list|,
name|Hashtable
name|environment
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|Hashtable
name|temp
init|=
operator|new
name|Hashtable
argument_list|(
name|environment
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
name|connectionPrefix
operator|+
name|name
operator|+
literal|"."
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|environment
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|key
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
comment|// Rename the key...
name|temp
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|key
operator|=
name|key
operator|.
name|substring
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|temp
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|createConnectionFactory
argument_list|(
name|temp
argument_list|)
return|;
block|}
specifier|protected
name|String
index|[]
name|getConnectionFactoryNames
parameter_list|(
name|Map
name|environment
parameter_list|)
block|{
name|String
name|factoryNames
init|=
operator|(
name|String
operator|)
name|environment
operator|.
name|get
argument_list|(
literal|"connectionFactoryNames"
argument_list|)
decl_stmt|;
if|if
condition|(
name|factoryNames
operator|!=
literal|null
condition|)
block|{
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|StringTokenizer
name|enumeration
init|=
operator|new
name|StringTokenizer
argument_list|(
name|factoryNames
argument_list|,
literal|","
argument_list|)
init|;
name|enumeration
operator|.
name|hasMoreTokens
argument_list|()
condition|;
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|enumeration
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|size
init|=
name|list
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|String
index|[]
name|answer
init|=
operator|new
name|String
index|[
name|size
index|]
decl_stmt|;
name|list
operator|.
name|toArray
argument_list|(
name|answer
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
return|return
name|defaultConnectionFactoryNames
return|;
block|}
specifier|protected
name|void
name|createQueues
parameter_list|(
name|Map
name|data
parameter_list|,
name|Hashtable
name|environment
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|environment
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|queuePrefix
argument_list|)
condition|)
block|{
name|String
name|jndiName
init|=
name|key
operator|.
name|substring
argument_list|(
name|queuePrefix
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
name|jndiName
argument_list|,
name|createQueue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|createTopics
parameter_list|(
name|Map
name|data
parameter_list|,
name|Hashtable
name|environment
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|environment
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|topicPrefix
argument_list|)
condition|)
block|{
name|String
name|jndiName
init|=
name|key
operator|.
name|substring
argument_list|(
name|topicPrefix
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
name|jndiName
argument_list|,
name|createTopic
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Factory method to create new Queue instances      */
specifier|protected
name|Queue
name|createQueue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Factory method to create new Topic instances      */
specifier|protected
name|Topic
name|createTopic
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Factory method to create a new connection factory from the given environment      */
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|(
name|Hashtable
name|environment
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|ActiveMQConnectionFactory
name|answer
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|putAll
argument_list|(
name|environment
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
name|String
name|getConnectionPrefix
parameter_list|()
block|{
return|return
name|connectionPrefix
return|;
block|}
specifier|public
name|void
name|setConnectionPrefix
parameter_list|(
name|String
name|connectionPrefix
parameter_list|)
block|{
name|this
operator|.
name|connectionPrefix
operator|=
name|connectionPrefix
expr_stmt|;
block|}
block|}
end_class

end_unit

