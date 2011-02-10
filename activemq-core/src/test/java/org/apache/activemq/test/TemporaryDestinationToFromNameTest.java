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
package|;
end_package

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
name|Session
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|EmbeddedBrokerAndConnectionTestSupport
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
name|jmx
operator|.
name|PurgeTest
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
comment|/**  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|TemporaryDestinationToFromNameTest
extends|extends
name|EmbeddedBrokerAndConnectionTestSupport
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
name|TemporaryDestinationToFromNameTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testCreateTemporaryQueueThenCreateAQueueFromItsName
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
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
name|Queue
name|tempQueue
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|tempQueue
operator|.
name|getQueueName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created queue named: "
operator|+
name|name
argument_list|)
expr_stmt|;
name|Queue
name|createdQueue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"created queue not equal to temporary queue"
argument_list|,
name|tempQueue
argument_list|,
name|createdQueue
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCreateTemporaryTopicThenCreateATopicFromItsName
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
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
name|Topic
name|tempTopic
init|=
name|session
operator|.
name|createTemporaryTopic
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|tempTopic
operator|.
name|getTopicName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created topic named: "
operator|+
name|name
argument_list|)
expr_stmt|;
name|Topic
name|createdTopic
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"created topic not equal to temporary topic"
argument_list|,
name|tempTopic
argument_list|,
name|createdTopic
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

