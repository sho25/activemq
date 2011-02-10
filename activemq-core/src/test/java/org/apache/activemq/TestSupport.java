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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|util
operator|.
name|Enumeration
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
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|BrokerService
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
name|region
operator|.
name|DestinationStatistics
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
name|region
operator|.
name|RegionBroker
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
name|ActiveMQDestination
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
name|ActiveMQMessage
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|PersistenceAdapter
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
name|store
operator|.
name|amq
operator|.
name|AMQPersistenceAdapter
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
name|store
operator|.
name|jdbc
operator|.
name|JDBCPersistenceAdapter
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
name|store
operator|.
name|kahadb
operator|.
name|KahaDBPersistenceAdapter
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
name|store
operator|.
name|memory
operator|.
name|MemoryPersistenceAdapter
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

begin_comment
comment|/**  * Useful base class for unit test cases  *   * @version $Revision: 1.5 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TestSupport
extends|extends
name|CombinationTestSupport
block|{
specifier|protected
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|protected
name|boolean
name|topic
init|=
literal|true
decl_stmt|;
specifier|public
name|PersistenceAdapterChoice
name|defaultPersistenceAdapter
init|=
name|PersistenceAdapterChoice
operator|.
name|KahaDB
decl_stmt|;
specifier|protected
name|ActiveMQMessage
name|createMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQMessage
argument_list|()
return|;
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|String
name|subject
parameter_list|)
block|{
if|if
condition|(
name|topic
condition|)
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|subject
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|subject
argument_list|)
return|;
block|}
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|()
block|{
return|return
name|createDestination
argument_list|(
name|getDestinationString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the name of the destination used in this test case      */
specifier|protected
name|String
name|getDestinationString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|(
literal|true
argument_list|)
return|;
block|}
comment|/**      * @param messsage      * @param firstSet      * @param secondSet      */
specifier|protected
name|void
name|assertTextMessagesEqual
parameter_list|(
name|String
name|messsage
parameter_list|,
name|Message
index|[]
name|firstSet
parameter_list|,
name|Message
index|[]
name|secondSet
parameter_list|)
throws|throws
name|JMSException
block|{
name|assertEquals
argument_list|(
literal|"Message count does not match: "
operator|+
name|messsage
argument_list|,
name|firstSet
operator|.
name|length
argument_list|,
name|secondSet
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|secondSet
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|m1
init|=
operator|(
name|TextMessage
operator|)
name|firstSet
index|[
name|i
index|]
decl_stmt|;
name|TextMessage
name|m2
init|=
operator|(
name|TextMessage
operator|)
name|secondSet
index|[
name|i
index|]
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Message "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|" did not match : "
operator|+
name|messsage
operator|+
literal|": expected {"
operator|+
name|m1
operator|+
literal|"}, but was {"
operator|+
name|m2
operator|+
literal|"}"
argument_list|,
name|m1
operator|==
literal|null
operator|^
name|m2
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Message "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|" did not match: "
operator|+
name|messsage
operator|+
literal|": expected {"
operator|+
name|m1
operator|+
literal|"}, but was {"
operator|+
name|m2
operator|+
literal|"}"
argument_list|,
name|m1
operator|.
name|getText
argument_list|()
argument_list|,
name|m2
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
return|;
block|}
comment|/**      * Factory method to create a new connection      */
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|public
name|ActiveMQConnectionFactory
name|getConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connectionFactory
operator|==
literal|null
condition|)
block|{
name|connectionFactory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a connection factory!"
argument_list|,
name|connectionFactory
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|connectionFactory
return|;
block|}
specifier|protected
name|String
name|getConsumerSubject
parameter_list|()
block|{
return|return
name|getSubject
argument_list|()
return|;
block|}
specifier|protected
name|String
name|getProducerSubject
parameter_list|()
block|{
return|return
name|getSubject
argument_list|()
return|;
block|}
specifier|protected
name|String
name|getSubject
parameter_list|()
block|{
return|return
name|getName
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|void
name|recursiveDelete
parameter_list|(
name|File
name|f
parameter_list|)
block|{
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|files
init|=
name|f
operator|.
name|listFiles
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|recursiveDelete
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|removeMessageStore
parameter_list|()
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"activemq.store.dir"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|recursiveDelete
argument_list|(
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"activemq.store.dir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"derby.system.home"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|recursiveDelete
argument_list|(
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"derby.system.home"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|DestinationStatistics
name|getDestinationStatistics
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|DestinationStatistics
name|result
init|=
literal|null
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Destination
name|dest
init|=
name|getDestination
argument_list|(
name|broker
argument_list|,
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|dest
operator|.
name|getDestinationStatistics
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Destination
name|getDestination
parameter_list|(
name|BrokerService
name|target
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Destination
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Destination
name|dest
range|:
name|getDestinationMap
argument_list|(
name|target
argument_list|,
name|destination
argument_list|)
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|dest
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|=
name|dest
expr_stmt|;
break|break;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|ActiveMQDestination
argument_list|,
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Destination
argument_list|>
name|getDestinationMap
parameter_list|(
name|BrokerService
name|target
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|RegionBroker
name|regionBroker
init|=
operator|(
name|RegionBroker
operator|)
name|target
operator|.
name|getRegionBroker
argument_list|()
decl_stmt|;
return|return
name|destination
operator|.
name|isQueue
argument_list|()
condition|?
name|regionBroker
operator|.
name|getQueueRegion
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
else|:
name|regionBroker
operator|.
name|getTopicRegion
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
return|;
block|}
specifier|public
specifier|static
enum|enum
name|PersistenceAdapterChoice
block|{
name|KahaDB
block|,
name|AMQ
block|,
name|JDBC
block|,
name|MEM
block|}
empty_stmt|;
specifier|public
name|PersistenceAdapter
name|setDefaultPersistenceAdapter
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|setPersistenceAdapter
argument_list|(
name|broker
argument_list|,
name|defaultPersistenceAdapter
argument_list|)
return|;
block|}
specifier|public
name|PersistenceAdapter
name|setPersistenceAdapter
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|PersistenceAdapterChoice
name|choice
parameter_list|)
throws|throws
name|IOException
block|{
name|PersistenceAdapter
name|adapter
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|choice
condition|)
block|{
case|case
name|AMQ
case|:
name|adapter
operator|=
operator|new
name|AMQPersistenceAdapter
argument_list|()
expr_stmt|;
break|break;
case|case
name|JDBC
case|:
name|adapter
operator|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
expr_stmt|;
break|break;
case|case
name|KahaDB
case|:
name|adapter
operator|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
expr_stmt|;
break|break;
case|case
name|MEM
case|:
name|adapter
operator|=
operator|new
name|MemoryPersistenceAdapter
argument_list|()
expr_stmt|;
break|break;
block|}
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
return|return
name|adapter
return|;
block|}
block|}
end_class

end_unit

