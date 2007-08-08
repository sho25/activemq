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
name|MessageConsumer
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsQueueSendReceiveMultipleConsumersTest
extends|extends
name|JmsQueueSendReceiveTest
block|{
name|MessageConsumer
name|consumer1
decl_stmt|;
name|MessageConsumer
name|consumer2
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|messageCount
operator|=
literal|5000
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|consumer1
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
name|consumer1
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|consumer2
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
name|consumer2
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

