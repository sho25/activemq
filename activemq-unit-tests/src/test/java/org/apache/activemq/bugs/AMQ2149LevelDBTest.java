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
name|bugs
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
name|broker
operator|.
name|BrokerService
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
name|leveldb
operator|.
name|LevelDBStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_comment
comment|//Ignored because there are now exceptions thrown on send when the broker is
end_comment

begin_comment
comment|//shutdown which cause the test to fail and need to be accounted for
end_comment

begin_comment
comment|//The parent test is also excluded in the pom.xml currently and not run
end_comment

begin_class
annotation|@
name|Ignore
specifier|public
class|class
name|AMQ2149LevelDBTest
extends|extends
name|AMQ2149Test
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configurePersistenceAdapter
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{
name|LevelDBStore
name|persistenceFactory
init|=
operator|new
name|LevelDBStore
argument_list|()
decl_stmt|;
name|persistenceFactory
operator|.
name|setDirectory
argument_list|(
name|dataDirFile
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceFactory
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

