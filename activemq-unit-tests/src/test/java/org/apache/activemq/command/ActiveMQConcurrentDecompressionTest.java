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
name|command
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|MessageListener
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
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * AMQ-6142  *  * This tests that all messages will be properly decompressed when there  * are several consumers  *  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQConcurrentDecompressionTest
block|{
specifier|private
specifier|volatile
name|AssertionError
name|assertionError
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|bytesMessageCorruption
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setBrokerName
argument_list|(
literal|"embedded"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://embedded"
argument_list|)
decl_stmt|;
name|connectionFactory
operator|.
name|setUseCompression
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Session
name|mySession
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
name|mySession
operator|.
name|createConsumer
argument_list|(
name|mySession
operator|.
name|createTopic
argument_list|(
literal|"foo.bar"
argument_list|)
argument_list|)
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1l
argument_list|,
operator|(
operator|(
name|ActiveMQBytesMessage
operator|)
name|message
operator|)
operator|.
name|getBodyLength
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"a"
operator|.
name|getBytes
argument_list|()
index|[
literal|0
index|]
argument_list|,
operator|(
operator|(
name|ActiveMQBytesMessage
operator|)
name|message
operator|)
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
decl||
name|Error
name|e
parameter_list|)
block|{
name|assertionError
operator|=
operator|new
name|AssertionError
argument_list|(
literal|"Exception in thread"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|Session
name|producerSession
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
name|MessageProducer
name|messageProducer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|producerSession
operator|.
name|createTopic
argument_list|(
literal|"foo.bar"
argument_list|)
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|BytesMessage
name|bytesMessage
init|=
name|producerSession
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|bytesMessage
operator|.
name|writeBytes
argument_list|(
literal|"a"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|messageProducer
operator|.
name|send
argument_list|(
name|bytesMessage
argument_list|)
expr_stmt|;
if|if
condition|(
name|assertionError
operator|!=
literal|null
condition|)
block|{
throw|throw
name|assertionError
throw|;
block|}
block|}
name|assertNull
argument_list|(
name|assertionError
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

