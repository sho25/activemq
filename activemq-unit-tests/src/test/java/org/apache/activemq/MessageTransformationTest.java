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
name|MapMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ObjectMessage
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
name|StreamMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryQueue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryTopic
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
name|javax
operator|.
name|jms
operator|.
name|Topic
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
name|command
operator|.
name|ActiveMQBytesMessage
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
name|ActiveMQStreamMessage
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
name|ActiveMQTempQueue
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
name|ActiveMQTempTopic
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
name|command
operator|.
name|ActiveMQTopic
import|;
end_import

begin_class
specifier|public
class|class
name|MessageTransformationTest
extends|extends
name|TestCase
block|{
comment|/**      * Sets up the resources of the unit test.      *       * @throws Exception      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{     }
comment|/**      * Clears up the resources used in the unit test.      */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{     }
comment|/**      * Tests transforming destinations into ActiveMQ's destination      * implementation.      */
specifier|public
name|void
name|testTransformDestination
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"Transforming a TempQueue destination to an ActiveMQTempQueue"
argument_list|,
name|ActiveMQMessageTransformation
operator|.
name|transformDestination
argument_list|(
operator|(
name|TemporaryQueue
operator|)
operator|new
name|ActiveMQTempQueue
argument_list|()
argument_list|)
operator|instanceof
name|ActiveMQTempQueue
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Transforming a TempTopic destination to an ActiveMQTempTopic"
argument_list|,
name|ActiveMQMessageTransformation
operator|.
name|transformDestination
argument_list|(
operator|(
name|TemporaryTopic
operator|)
operator|new
name|ActiveMQTempTopic
argument_list|()
argument_list|)
operator|instanceof
name|ActiveMQTempTopic
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Transforming a Queue destination to an ActiveMQQueue"
argument_list|,
name|ActiveMQMessageTransformation
operator|.
name|transformDestination
argument_list|(
operator|(
name|Queue
operator|)
operator|new
name|ActiveMQQueue
argument_list|()
argument_list|)
operator|instanceof
name|ActiveMQQueue
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Transforming a Topic destination to an ActiveMQTopic"
argument_list|,
name|ActiveMQMessageTransformation
operator|.
name|transformDestination
argument_list|(
operator|(
name|Topic
operator|)
operator|new
name|ActiveMQTopic
argument_list|()
argument_list|)
operator|instanceof
name|ActiveMQTopic
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Transforming a Destination to an ActiveMQDestination"
argument_list|,
name|ActiveMQMessageTransformation
operator|.
name|transformDestination
argument_list|(
operator|(
name|ActiveMQDestination
operator|)
operator|new
name|ActiveMQTopic
argument_list|()
argument_list|)
operator|instanceof
name|ActiveMQDestination
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests transforming messages into ActiveMQ's message implementation.      */
specifier|public
name|void
name|testTransformMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"Transforming a BytesMessage message into an ActiveMQBytesMessage"
argument_list|,
name|ActiveMQMessageTransformation
operator|.
name|transformMessage
argument_list|(
operator|(
name|BytesMessage
operator|)
operator|new
name|ActiveMQBytesMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|ActiveMQBytesMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Transforming a MapMessage message to an ActiveMQMapMessage"
argument_list|,
name|ActiveMQMessageTransformation
operator|.
name|transformMessage
argument_list|(
operator|(
name|MapMessage
operator|)
operator|new
name|ActiveMQMapMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|ActiveMQMapMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Transforming an ObjectMessage message to an ActiveMQObjectMessage"
argument_list|,
name|ActiveMQMessageTransformation
operator|.
name|transformMessage
argument_list|(
operator|(
name|ObjectMessage
operator|)
operator|new
name|ActiveMQObjectMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|ActiveMQObjectMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Transforming a StreamMessage message to an ActiveMQStreamMessage"
argument_list|,
name|ActiveMQMessageTransformation
operator|.
name|transformMessage
argument_list|(
operator|(
name|StreamMessage
operator|)
operator|new
name|ActiveMQStreamMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|ActiveMQStreamMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Transforming a TextMessage message to an ActiveMQTextMessage"
argument_list|,
name|ActiveMQMessageTransformation
operator|.
name|transformMessage
argument_list|(
operator|(
name|TextMessage
operator|)
operator|new
name|ActiveMQTextMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|ActiveMQTextMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Transforming an ActiveMQMessage message to an ActiveMQMessage"
argument_list|,
name|ActiveMQMessageTransformation
operator|.
name|transformMessage
argument_list|(
operator|new
name|ActiveMQMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|ActiveMQMessage
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

