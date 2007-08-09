begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|ActiveMQQueue
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
comment|/**  *   * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|NewConsumerCreatesDestinationTest
extends|extends
name|EmbeddedBrokerAndConnectionTestSupport
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NewConsumerCreatesDestinationTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ActiveMQQueue
name|wildcard
decl_stmt|;
specifier|public
name|void
name|testNewConsumerCausesNewDestinationToBeAutoCreated
parameter_list|()
throws|throws
name|Exception
block|{
comment|// lets create a wildcard thats kinda like those used by Virtual Topics
name|String
name|wildcardText
init|=
literal|"org.*"
operator|+
name|getDestinationString
argument_list|()
operator|.
name|substring
argument_list|(
literal|"org.apache"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|wildcard
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|wildcardText
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Using wildcard: "
operator|+
name|wildcard
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"on destination: "
operator|+
name|destination
argument_list|)
expr_stmt|;
name|assertDestinationCreated
argument_list|(
name|destination
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertDestinationCreated
argument_list|(
name|wildcard
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|assertDestinationCreated
argument_list|(
name|destination
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertDestinationCreated
argument_list|(
name|wildcard
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertDestinationCreated
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|boolean
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|Set
name|answer
init|=
name|broker
operator|.
name|getBroker
argument_list|()
operator|.
name|getDestinations
argument_list|(
operator|(
name|ActiveMQDestination
operator|)
name|destination
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|expected
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Size of found destinations: "
operator|+
name|answer
argument_list|,
name|size
argument_list|,
name|answer
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

