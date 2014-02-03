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
name|util
operator|.
name|MessageIdList
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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|MessageConsumer
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|TimeUnit
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
name|assertTrue
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|AbortSlowConsumer1Test
extends|extends
name|AbortSlowConsumerBase
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
name|AbortSlowConsumer1Test
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}-{1}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|getTestParameters
parameter_list|()
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|testParameters
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|Boolean
index|[]
name|booleanValues
init|=
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|TRUE
block|}
decl_stmt|;
for|for
control|(
name|Boolean
name|abortConnection
range|:
name|booleanValues
control|)
block|{
for|for
control|(
name|Boolean
name|topic
range|:
name|booleanValues
control|)
block|{
name|Boolean
index|[]
name|pair
init|=
block|{
name|abortConnection
block|,
name|topic
block|}
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|">>>>> in getTestparameters, adding {}, {}"
argument_list|,
name|abortConnection
argument_list|,
name|topic
argument_list|)
expr_stmt|;
name|testParameters
operator|.
name|add
argument_list|(
name|pair
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|testParameters
return|;
block|}
specifier|public
name|AbortSlowConsumer1Test
parameter_list|(
name|Boolean
name|abortConnection
parameter_list|,
name|Boolean
name|topic
parameter_list|)
block|{
name|this
operator|.
name|abortConnection
operator|=
name|abortConnection
expr_stmt|;
name|this
operator|.
name|topic
operator|=
name|topic
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSlowConsumerIsAborted
parameter_list|()
throws|throws
name|Exception
block|{
name|startConsumers
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|Entry
argument_list|<
name|MessageConsumer
argument_list|,
name|MessageIdList
argument_list|>
name|consumertoAbort
init|=
name|consumers
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|consumertoAbort
operator|.
name|getValue
argument_list|()
operator|.
name|setProcessingDelay
argument_list|(
literal|8
operator|*
literal|1000
argument_list|)
expr_stmt|;
for|for
control|(
name|Connection
name|c
range|:
name|connections
control|)
block|{
name|c
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|startProducers
argument_list|(
name|destination
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|consumertoAbort
operator|.
name|getValue
argument_list|()
operator|.
name|assertMessagesReceived
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|consumertoAbort
operator|.
name|getValue
argument_list|()
operator|.
name|assertAtMostMessagesReceived
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testAbortAlreadyClosedConsumers
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|conn
init|=
name|createConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|conn
argument_list|)
expr_stmt|;
name|Session
name|sess
init|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
specifier|final
name|MessageConsumer
name|consumer
init|=
name|sess
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|startProducers
argument_list|(
name|destination
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"closing consumer: "
operator|+
name|consumer
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no exceptions : "
operator|+
name|exceptions
operator|.
name|toArray
argument_list|()
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testAbortAlreadyClosedConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|conn
init|=
name|createConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|Session
name|sess
init|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|sess
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|startProducers
argument_list|(
name|destination
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"closing connection: "
operator|+
name|conn
argument_list|)
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no exceptions : "
operator|+
name|exceptions
operator|.
name|toArray
argument_list|()
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
