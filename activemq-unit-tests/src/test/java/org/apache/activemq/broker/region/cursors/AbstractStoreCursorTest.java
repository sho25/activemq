begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|region
operator|.
name|cursors
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
name|MessageId
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|FutureTask
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_class
specifier|public
class|class
name|AbstractStoreCursorTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testGotToStore
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQMessage
name|message
init|=
operator|new
name|ActiveMQMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setRecievedByDFBridge
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MessageId
name|messageId
init|=
operator|new
name|MessageId
argument_list|()
decl_stmt|;
name|message
operator|.
name|setMessageId
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
name|FutureTask
argument_list|<
name|Long
argument_list|>
name|futureTask
init|=
operator|new
name|FutureTask
argument_list|<
name|Long
argument_list|>
argument_list|(
operator|new
name|Callable
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|call
parameter_list|()
block|{
return|return
literal|0l
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|messageId
operator|.
name|setFutureOrSequenceLong
argument_list|(
name|futureTask
argument_list|)
expr_stmt|;
name|futureTask
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|AbstractStoreCursor
operator|.
name|gotToTheStore
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

