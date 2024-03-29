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
name|spring
package|;
end_package

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
name|TestCase
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

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|AbstractApplicationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|ClassPathXmlApplicationContext
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|SpringTestSupport
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SpringTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|AbstractApplicationContext
name|context
decl_stmt|;
specifier|protected
name|SpringConsumer
name|consumer
decl_stmt|;
specifier|protected
name|SpringProducer
name|producer
decl_stmt|;
comment|/**      * assert method that is used by all the test method to send and receive messages      * based on each spring configuration.      *      * @param config      * @throws Exception      */
specifier|protected
name|void
name|assertSenderConfig
parameter_list|(
name|String
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|SpringTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|=
operator|new
name|ClassPathXmlApplicationContext
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|consumer
operator|=
operator|(
name|SpringConsumer
operator|)
name|context
operator|.
name|getBean
argument_list|(
literal|"consumer"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Found a valid consumer"
argument_list|,
name|consumer
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Wait a little to drain any left over messages.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|flushMessages
argument_list|()
expr_stmt|;
name|producer
operator|=
operator|(
name|SpringProducer
operator|)
name|context
operator|.
name|getBean
argument_list|(
literal|"producer"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Found a valid producer"
argument_list|,
name|producer
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// lets sleep a little to give the JMS time to dispatch stuff
name|consumer
operator|.
name|waitForMessagesToArrive
argument_list|(
name|producer
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// now lets check that the consumer has received some messages
name|List
argument_list|<
name|Message
argument_list|>
name|messages
init|=
name|consumer
operator|.
name|flushMessages
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumer has received messages...."
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Message
argument_list|>
name|iter
init|=
name|messages
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
name|Object
name|message
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Message count"
argument_list|,
name|producer
operator|.
name|getMessageCount
argument_list|()
argument_list|,
name|messages
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Clean up method.      *      * @throws Exception      */
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|consumer
operator|!=
literal|null
condition|)
block|{
name|consumer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|producer
operator|!=
literal|null
condition|)
block|{
name|producer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

