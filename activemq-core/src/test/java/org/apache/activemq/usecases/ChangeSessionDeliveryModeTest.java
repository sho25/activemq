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
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
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
name|MessageConsumer
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
name|test
operator|.
name|TestSupport
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|ChangeSessionDeliveryModeTest
extends|extends
name|TestSupport
implements|implements
name|MessageListener
block|{
comment|/**      * test following condition- which are defined by JMS Spec 1.1:      * MessageConsumers cannot use a MessageListener and receive() from the same      * session      *       * @throws Exception      */
specifier|public
name|void
name|testDoChangeSessionDeliveryMode
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|destination
init|=
name|createDestination
argument_list|(
literal|"foo.bar"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
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
name|MessageConsumer
name|consumer1
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|consumer1
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|JMSException
name|jmsEx
init|=
literal|null
decl_stmt|;
name|MessageConsumer
name|consumer2
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
try|try
block|{
name|consumer2
operator|.
name|receive
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not receive expected exception."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|IllegalStateException
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{     }
block|}
end_class

end_unit

