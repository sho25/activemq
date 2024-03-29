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
name|test
operator|.
name|retroactive
package|;
end_package

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
name|MessageQuery
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
name|Message
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

begin_comment
comment|/**  *  *   */
end_comment

begin_class
specifier|public
class|class
name|DummyMessageQuery
implements|implements
name|MessageQuery
block|{
specifier|public
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|10
decl_stmt|;
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
name|DummyMessageQuery
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|execute
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
name|MessageListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initial query is creating: "
operator|+
name|MESSAGE_COUNT
operator|+
literal|" messages"
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQTextMessage
name|message
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Initial message: "
operator|+
name|i
operator|+
literal|" loaded from query"
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|validateUpdate
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

