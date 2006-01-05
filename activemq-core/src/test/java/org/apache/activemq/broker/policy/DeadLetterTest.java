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
name|policy
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
name|RedeliveryPolicy
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
name|Message
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DeadLetterTest
extends|extends
name|DeadLetterTestSupport
block|{
specifier|private
name|int
name|rollbackCount
decl_stmt|;
specifier|protected
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnection
name|amqConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connection
decl_stmt|;
name|rollbackCount
operator|=
name|amqConnection
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
operator|+
literal|1
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Will redeliver messages: "
operator|+
name|rollbackCount
operator|+
literal|" times"
argument_list|)
expr_stmt|;
name|makeConsumer
argument_list|()
expr_stmt|;
name|makeDlqConsumer
argument_list|()
expr_stmt|;
name|sendMessages
argument_list|()
expr_stmt|;
comment|// now lets receive and rollback N times
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
name|consumeAndRollback
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
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
name|msg
init|=
name|dlqConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertMessage
argument_list|(
name|msg
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should be a DLQ message for loop: "
operator|+
name|i
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|consumeAndRollback
parameter_list|(
name|int
name|messageCounter
parameter_list|)
throws|throws
name|Exception
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
name|rollbackCount
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No message received for message: "
operator|+
name|messageCounter
operator|+
literal|" and rollback loop: "
operator|+
name|i
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertMessage
argument_list|(
name|message
argument_list|,
name|messageCounter
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Rolled back: "
operator|+
name|rollbackCount
operator|+
literal|" times"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|transactedMode
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|answer
init|=
name|super
operator|.
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|RedeliveryPolicy
name|policy
init|=
operator|new
name|RedeliveryPolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setMaximumRedeliveries
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setBackOffMultiplier
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setInitialRedeliveryDelay
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setUseExponentialBackOff
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setRedeliveryPolicy
argument_list|(
name|policy
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|Destination
name|createDlqDestination
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ActiveMQ.DLQ"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

