begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|usecases
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|RESTLoadTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|volatile
name|int
name|counter
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|testREST
parameter_list|()
block|{
name|int
name|HowManyMessages
init|=
literal|60000
decl_stmt|;
name|TestConsumerThread
name|consumer
init|=
operator|new
name|TestConsumerThread
argument_list|(
name|this
argument_list|,
name|HowManyMessages
argument_list|)
decl_stmt|;
name|TestProducerThread
name|producer
init|=
operator|new
name|TestProducerThread
argument_list|(
name|this
argument_list|,
name|HowManyMessages
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|start
argument_list|()
expr_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
name|counter
operator|>
literal|0
condition|)
block|{         }
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Produced:"
operator|+
name|producer
operator|.
name|success
operator|+
literal|" Consumed:"
operator|+
name|consumer
operator|.
name|success
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|RESTLoadTest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

