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
name|streams
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|HashMap
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
name|Map
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
name|AtomicBoolean
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
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|ActiveMQConnection
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
name|ActiveMQInputStream
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
name|ActiveMQOutputStream
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
name|JmsTestSupport
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

begin_comment
comment|/**  * JMSInputStreamTest  */
end_comment

begin_class
specifier|public
class|class
name|JMSInputStreamTest
extends|extends
name|JmsTestSupport
block|{
specifier|public
name|Destination
name|destination
decl_stmt|;
specifier|protected
name|DataOutputStream
name|out
decl_stmt|;
specifier|protected
name|DataInputStream
name|in
decl_stmt|;
specifier|private
name|ActiveMQConnection
name|connection2
decl_stmt|;
specifier|private
name|ActiveMQInputStream
name|amqIn
decl_stmt|;
specifier|private
name|ActiveMQOutputStream
name|amqOut
decl_stmt|;
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|JMSInputStreamTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombos
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TEST.TOPIC"
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setUpConnection
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|JMSException
block|{
name|connection2
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
name|connection2
argument_list|)
expr_stmt|;
if|if
condition|(
name|props
operator|!=
literal|null
condition|)
block|{
name|amqOut
operator|=
operator|(
name|ActiveMQOutputStream
operator|)
name|connection
operator|.
name|createOutputStream
argument_list|(
name|destination
argument_list|,
name|props
argument_list|,
name|Message
operator|.
name|DEFAULT_DELIVERY_MODE
argument_list|,
name|Message
operator|.
name|DEFAULT_PRIORITY
argument_list|,
name|Message
operator|.
name|DEFAULT_TIME_TO_LIVE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|amqOut
operator|=
operator|(
name|ActiveMQOutputStream
operator|)
name|connection
operator|.
name|createOutputStream
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
name|out
operator|=
operator|new
name|DataOutputStream
argument_list|(
name|amqOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|timeout
operator|==
operator|-
literal|1
condition|)
block|{
name|amqIn
operator|=
operator|(
name|ActiveMQInputStream
operator|)
name|connection2
operator|.
name|createInputStream
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|amqIn
operator|=
operator|(
name|ActiveMQInputStream
operator|)
name|connection2
operator|.
name|createInputStream
argument_list|(
name|destination
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
name|amqIn
argument_list|)
expr_stmt|;
block|}
comment|/*      * @see TestCase#tearDown()      */
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test for AMQ-3010      */
specifier|public
name|void
name|testInputStreamTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|timeout
init|=
literal|500
decl_stmt|;
name|setUpConnection
argument_list|(
literal|null
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
try|try
block|{
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ActiveMQInputStream
operator|.
name|ReadTimeoutException
name|e
parameter_list|)
block|{
comment|// timeout reached, everything ok
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Test for AMQ-2988
specifier|public
name|void
name|testStreamsWithProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|name1
init|=
literal|"PROPERTY_1"
decl_stmt|;
name|String
name|name2
init|=
literal|"PROPERTY_2"
decl_stmt|;
name|String
name|value1
init|=
literal|"VALUE_1"
decl_stmt|;
name|String
name|value2
init|=
literal|"VALUE_2"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jmsProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|jmsProperties
operator|.
name|put
argument_list|(
name|name1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|jmsProperties
operator|.
name|put
argument_list|(
name|name2
argument_list|,
name|value2
argument_list|)
expr_stmt|;
name|setUpConnection
argument_list|(
name|jmsProperties
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
literal|2.3f
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|readFloat
argument_list|()
operator|==
literal|2.3f
argument_list|)
expr_stmt|;
name|String
name|str
init|=
literal|"this is a test string"
decl_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|readUTF
argument_list|()
operator|.
name|equals
argument_list|(
name|str
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// check properties before we try to read the stream
name|checkProperties
argument_list|(
name|jmsProperties
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
operator|==
name|i
argument_list|)
expr_stmt|;
block|}
comment|// check again after read was done
name|checkProperties
argument_list|(
name|jmsProperties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStreamsWithPropertiesOnlyOnFirstMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|name1
init|=
literal|"PROPERTY_1"
decl_stmt|;
name|String
name|name2
init|=
literal|"PROPERTY_2"
decl_stmt|;
name|String
name|value1
init|=
literal|"VALUE_1"
decl_stmt|;
name|String
name|value2
init|=
literal|"VALUE_2"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jmsProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|jmsProperties
operator|.
name|put
argument_list|(
name|name1
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|jmsProperties
operator|.
name|put
argument_list|(
name|name2
argument_list|,
name|value2
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|dest
init|=
operator|(
name|ActiveMQDestination
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|dest
operator|.
name|isQueue
argument_list|()
condition|)
block|{
name|destination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|dest
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|"?producer.addPropertiesOnFirstMsgOnly=true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destination
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
name|dest
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|"?producer.addPropertiesOnFirstMsgOnly=true"
argument_list|)
expr_stmt|;
block|}
name|setUpConnection
argument_list|(
name|jmsProperties
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|amqOut
operator|.
name|isAddPropertiesOnFirstMsgOnly
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
literal|2.3f
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|readFloat
argument_list|()
operator|==
literal|2.3f
argument_list|)
expr_stmt|;
name|String
name|str
init|=
literal|"this is a test string"
decl_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|readUTF
argument_list|()
operator|.
name|equals
argument_list|(
name|str
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// check properties before we try to read the stream
name|checkProperties
argument_list|(
name|jmsProperties
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
operator|==
name|i
argument_list|)
expr_stmt|;
block|}
comment|// check again after read was done
name|checkProperties
argument_list|(
name|jmsProperties
argument_list|)
expr_stmt|;
block|}
comment|// check if the received stream has the properties set
comment|// Test for AMQ-2988
specifier|private
name|void
name|checkProperties
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jmsProperties
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|receivedJmsProps
init|=
name|amqIn
operator|.
name|getJMSProperties
argument_list|()
decl_stmt|;
comment|// we should at least have the same amount or more properties
name|assertTrue
argument_list|(
name|jmsProperties
operator|.
name|size
argument_list|()
operator|<=
name|receivedJmsProps
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// check the properties to see if we have everything in there
name|Iterator
argument_list|<
name|String
argument_list|>
name|propsIt
init|=
name|jmsProperties
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|propsIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|propsIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|receivedJmsProps
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jmsProperties
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
name|receivedJmsProps
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testLarge
parameter_list|()
throws|throws
name|Exception
block|{
name|setUpConnection
argument_list|(
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|int
name|testData
init|=
literal|23
decl_stmt|;
specifier|final
name|int
name|dataLength
init|=
literal|4096
decl_stmt|;
specifier|final
name|int
name|count
init|=
literal|1024
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|dataLength
index|]
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
name|testData
expr_stmt|;
block|}
specifier|final
name|AtomicBoolean
name|complete
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Thread
name|runner
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|count
condition|;
name|x
operator|++
control|)
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|2048
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|b
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
name|b
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|b
index|[
name|i
index|]
operator|==
name|testData
argument_list|)
expr_stmt|;
block|}
block|}
name|complete
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|complete
init|)
block|{
name|complete
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|runner
operator|.
name|start
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|complete
init|)
block|{
if|if
condition|(
operator|!
name|complete
operator|.
name|get
argument_list|()
condition|)
block|{
name|complete
operator|.
name|wait
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|complete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStreams
parameter_list|()
throws|throws
name|Exception
block|{
name|setUpConnection
argument_list|(
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
literal|2.3f
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|readFloat
argument_list|()
operator|==
literal|2.3f
argument_list|)
expr_stmt|;
name|String
name|str
init|=
literal|"this is a test string"
decl_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|readUTF
argument_list|()
operator|.
name|equals
argument_list|(
name|str
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
operator|==
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
