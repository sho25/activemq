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
name|usecases
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
name|broker
operator|.
name|policy
operator|.
name|IndividualDeadLetterViaXmlTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ConsumeTopicPrefetchTest
extends|extends
name|ProducerConsumerTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ConsumeTopicPrefetchTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|prefetchSize
init|=
literal|100
decl_stmt|;
specifier|protected
name|String
index|[]
name|messageTexts
decl_stmt|;
specifier|protected
name|long
name|consumerTimeout
init|=
literal|10000L
decl_stmt|;
specifier|public
name|void
name|testSendPrefetchSize
parameter_list|()
throws|throws
name|JMSException
block|{
name|testWithMessageCount
argument_list|(
name|prefetchSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSendDoublePrefetchSize
parameter_list|()
throws|throws
name|JMSException
block|{
name|testWithMessageCount
argument_list|(
name|prefetchSize
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSendPrefetchSizePlusOne
parameter_list|()
throws|throws
name|JMSException
block|{
name|testWithMessageCount
argument_list|(
name|prefetchSize
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|testWithMessageCount
parameter_list|(
name|int
name|messageCount
parameter_list|)
throws|throws
name|JMSException
block|{
name|makeMessages
argument_list|(
name|messageCount
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"About to send and receive: "
operator|+
name|messageCount
operator|+
literal|" on destination: "
operator|+
name|destination
operator|+
literal|" of type: "
operator|+
name|destination
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|messageTexts
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|// lets consume them in two fetch batches
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|consumeMessge
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|super
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setQueuePrefetch
argument_list|(
name|prefetchSize
argument_list|)
expr_stmt|;
name|connection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setTopicPrefetch
argument_list|(
name|prefetchSize
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
specifier|protected
name|void
name|consumeMessge
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|JMSException
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|consumerTimeout
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a message by now for message: "
operator|+
name|i
argument_list|,
name|message
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be a TextMessage: "
operator|+
name|message
argument_list|,
name|message
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Message content"
argument_list|,
name|messageTexts
index|[
name|i
index|]
argument_list|,
name|textMessage
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|makeMessages
parameter_list|(
name|int
name|messageCount
parameter_list|)
block|{
name|messageTexts
operator|=
operator|new
name|String
index|[
name|messageCount
index|]
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|messageTexts
index|[
name|i
index|]
operator|=
literal|"Message for test: + "
operator|+
name|getName
argument_list|()
operator|+
literal|" = "
operator|+
name|i
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

