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
name|bugs
package|;
end_package

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
name|Message
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
name|ActiveMQSession
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
name|BrokerTestSupport
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
name|policy
operator|.
name|PolicyEntry
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
name|ActiveMQMapMessage
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
name|ActiveMQObjectMessage
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
name|ActiveMQTextMessage
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
name|usecases
operator|.
name|MyObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * AMQ-6477 changes the behavior to only clear memory if the marshalled state exists  * so this test no longer works  */
end_comment

begin_class
annotation|@
name|Ignore
specifier|public
class|class
name|AMQ2103Test
extends|extends
name|BrokerTestSupport
block|{
specifier|static
name|PolicyEntry
name|reduceMemoryFootprint
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
static|static
block|{
name|reduceMemoryFootprint
operator|.
name|setReduceMemoryFootprint
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PolicyEntry
name|defaultPolicy
init|=
name|reduceMemoryFootprint
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|PolicyEntry
name|getDefaultPolicy
parameter_list|()
block|{
return|return
name|defaultPolicy
return|;
block|}
specifier|public
name|void
name|initCombosForTestVerifyMarshalledStateIsCleared
parameter_list|()
throws|throws
name|Exception
block|{
name|addCombinationValues
argument_list|(
literal|"defaultPolicy"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|defaultPolicy
block|,
literal|null
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|AMQ2103Test
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**      * use mem persistence so no marshaling,      * reduceMemoryFootprint on/off that will reduce memory by whacking the marshaled state      * With vm transport and deferred serialisation and no persistence (mem persistence),      * we see the message as sent by the client so we can validate the contents against      * the policy      *      *      * @throws Exception      */
specifier|public
name|void
name|testVerifyMarshalledStateIsCleared
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setOptimizedMessageDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setObjectMessageSerializationDefered
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
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
name|ActiveMQDestination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"testQ"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
specifier|final
name|MyObject
name|obj
init|=
operator|new
name|MyObject
argument_list|(
literal|"A message"
argument_list|)
decl_stmt|;
name|ActiveMQObjectMessage
name|m1
init|=
operator|(
name|ActiveMQObjectMessage
operator|)
name|session
operator|.
name|createObjectMessage
argument_list|()
decl_stmt|;
name|m1
operator|.
name|setObject
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m1
argument_list|)
expr_stmt|;
name|ActiveMQTextMessage
name|m2
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|m2
operator|.
name|setText
argument_list|(
literal|"Test Message Payload."
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m2
argument_list|)
expr_stmt|;
name|ActiveMQMapMessage
name|m3
init|=
operator|new
name|ActiveMQMapMessage
argument_list|()
decl_stmt|;
name|m3
operator|.
name|setString
argument_list|(
literal|"text"
argument_list|,
literal|"my message"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m3
argument_list|)
expr_stmt|;
name|Message
name|m
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|maxWait
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m1
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|m
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|instanceof
name|ActiveMQObjectMessage
argument_list|)
expr_stmt|;
if|if
condition|(
name|getDefaultPolicy
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assertNull
argument_list|(
literal|"object data cleared by reduceMemoryFootprint (and never marshalled as using mem persistence)"
argument_list|,
operator|(
operator|(
name|ActiveMQObjectMessage
operator|)
name|m
operator|)
operator|.
name|getObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// verify no serialisation via vm transport
name|assertEquals
argument_list|(
literal|"writeObject called"
argument_list|,
literal|0
argument_list|,
name|obj
operator|.
name|getWriteObjectCalled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"readObject called"
argument_list|,
literal|0
argument_list|,
name|obj
operator|.
name|getReadObjectCalled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"readObjectNoData called"
argument_list|,
literal|0
argument_list|,
name|obj
operator|.
name|getReadObjectNoDataCalled
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|=
name|consumer
operator|.
name|receive
argument_list|(
name|maxWait
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m2
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|m
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|instanceof
name|ActiveMQTextMessage
argument_list|)
expr_stmt|;
if|if
condition|(
name|getDefaultPolicy
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assertNull
argument_list|(
literal|"text cleared by reduceMemoryFootprint (and never marshalled as using mem persistence)"
argument_list|,
operator|(
operator|(
name|ActiveMQTextMessage
operator|)
name|m
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|m
operator|=
name|consumer
operator|.
name|receive
argument_list|(
name|maxWait
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m3
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|m
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|instanceof
name|ActiveMQMapMessage
argument_list|)
expr_stmt|;
if|if
condition|(
name|getDefaultPolicy
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assertNull
argument_list|(
literal|"text cleared by reduceMemoryFootprint (and never marshalled as using mem persistence)"
argument_list|,
operator|(
operator|(
name|ActiveMQMapMessage
operator|)
name|m
operator|)
operator|.
name|getStringProperty
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

