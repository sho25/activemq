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
package|;
end_package

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
specifier|public
class|class
name|BrokerServiceTest
block|{
specifier|static
class|class
name|Hook
implements|implements
name|Runnable
block|{
name|boolean
name|invoked
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|invoked
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|removedPreShutdownHooksShouldNotBeInvokedWhenStopping
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
specifier|final
name|Hook
name|hook
init|=
operator|new
name|Hook
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|addPreShutdownHook
argument_list|(
name|hook
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|removePreShutdownHook
argument_list|(
name|hook
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Removed pre-shutdown hook should not have been invoked"
argument_list|,
name|hook
operator|.
name|invoked
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldInvokePreShutdownHooksBeforeStopping
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
specifier|final
name|Hook
name|hook
init|=
operator|new
name|Hook
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|addPreShutdownHook
argument_list|(
name|hook
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Pre-shutdown hook should have been invoked"
argument_list|,
name|hook
operator|.
name|invoked
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

