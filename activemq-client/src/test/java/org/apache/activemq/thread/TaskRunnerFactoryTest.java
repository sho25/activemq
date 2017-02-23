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
name|thread
package|;
end_package

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
name|Collections
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
name|concurrent
operator|.
name|CountDownLatch
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
name|ExecutorService
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
name|Executors
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

begin_class
specifier|public
class|class
name|TaskRunnerFactoryTest
block|{
comment|/**      * AMQ-6602 test      * Test contention on createTaskRunner() to make sure that all threads end up      * using a PooledTaskRunner      *      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testConcurrentTaskRunnerCreation
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|TaskRunnerFactory
name|factory
init|=
operator|new
name|TaskRunnerFactory
argument_list|()
decl_stmt|;
specifier|final
name|ExecutorService
name|service
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch1
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch2
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|TaskRunner
argument_list|>
name|runners
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|service
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|latch1
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|runners
operator|.
name|add
argument_list|(
name|factory
operator|.
name|createTaskRunner
argument_list|(
operator|new
name|Task
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|iterate
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|"task"
argument_list|)
argument_list|)
expr_stmt|;
name|latch2
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|latch1
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|latch2
operator|.
name|await
argument_list|()
expr_stmt|;
for|for
control|(
name|TaskRunner
name|runner
range|:
name|runners
control|)
block|{
name|assertTrue
argument_list|(
name|runner
operator|instanceof
name|PooledTaskRunner
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

