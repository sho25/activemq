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
name|net
operator|.
name|URI
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
name|Collections
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|ConnectionFactory
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
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
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
name|BrokerFactory
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_comment
comment|/**  * Test cases used to test the JMS message consumer.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|JmsTestSupport
extends|extends
name|CombinationTestSupport
block|{
specifier|static
specifier|final
specifier|private
name|AtomicLong
name|TEST_COUNTER
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|public
name|String
name|userName
decl_stmt|;
specifier|public
name|String
name|password
decl_stmt|;
specifier|public
name|String
name|messageTextPrefix
init|=
literal|""
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|factory
decl_stmt|;
specifier|protected
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|Connection
argument_list|>
name|connections
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Connection
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// /////////////////////////////////////////////////////////////////
comment|//
comment|// Test support methods.
comment|//
comment|// /////////////////////////////////////////////////////////////////
specifier|protected
name|ActiveMQDestination
name|createDestination
parameter_list|(
name|Session
name|session
parameter_list|,
name|byte
name|type
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|testMethod
init|=
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|testMethod
operator|.
name|indexOf
argument_list|(
literal|" "
argument_list|)
operator|>
literal|0
condition|)
block|{
name|testMethod
operator|=
name|testMethod
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|testMethod
operator|.
name|indexOf
argument_list|(
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|name
init|=
literal|"TEST."
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|testMethod
operator|+
literal|"."
operator|+
name|TEST_COUNTER
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
case|:
return|return
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createQueue
argument_list|(
name|name
argument_list|)
return|;
case|case
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
case|:
return|return
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createTopic
argument_list|(
name|name
argument_list|)
return|;
case|case
name|ActiveMQDestination
operator|.
name|TEMP_QUEUE_TYPE
case|:
return|return
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createTemporaryQueue
argument_list|()
return|;
case|case
name|ActiveMQDestination
operator|.
name|TEMP_TOPIC_TYPE
case|:
return|return
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createTemporaryTopic
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|void
name|sendMessages
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|sendMessages
argument_list|(
name|connection
argument_list|,
name|destination
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|sendMessages
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|JMSException
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|sendMessages
argument_list|(
name|session
argument_list|,
name|destination
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|sendMessages
parameter_list|(
name|Session
name|session
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|JMSException
block|{
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|messageTextPrefix
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker://()/localhost?persistent=false"
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"basedir"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"basedir"
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|factory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|connections
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
name|Connection
name|conn
init|=
operator|(
name|Connection
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{             }
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|safeClose
parameter_list|(
name|Connection
name|c
parameter_list|)
block|{
try|try
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{         }
block|}
specifier|protected
name|void
name|safeClose
parameter_list|(
name|Session
name|s
parameter_list|)
block|{
try|try
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{         }
block|}
specifier|protected
name|void
name|safeClose
parameter_list|(
name|MessageConsumer
name|c
parameter_list|)
block|{
try|try
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{         }
block|}
specifier|protected
name|void
name|safeClose
parameter_list|(
name|MessageProducer
name|p
parameter_list|)
block|{
try|try
block|{
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{         }
block|}
specifier|protected
name|void
name|profilerPause
parameter_list|(
name|String
name|prompt
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"profiler"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|pause
argument_list|(
name|prompt
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|pause
parameter_list|(
name|String
name|prompt
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|prompt
operator|+
literal|"> Press enter to continue: "
argument_list|)
expr_stmt|;
while|while
condition|(
name|System
operator|.
name|in
operator|.
name|read
argument_list|()
operator|!=
literal|'\n'
condition|)
block|{         }
block|}
block|}
end_class

end_unit

