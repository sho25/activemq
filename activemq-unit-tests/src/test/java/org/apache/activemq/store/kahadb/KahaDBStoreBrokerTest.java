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
name|store
operator|.
name|kahadb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|broker
operator|.
name|BrokerTest
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
name|util
operator|.
name|IOHelper
import|;
end_import

begin_comment
comment|/**  * Once the wire format is completed we can test against real persistence storage.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|KahaDBStoreBrokerTest
extends|extends
name|BrokerTest
block|{
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|KahaDBStore
name|kaha
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
literal|"target/activemq-data/kahadb"
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|deleteChildren
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|BrokerService
name|createRestartedBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|KahaDBStore
name|kaha
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|kaha
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/kahadb"
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|KahaDBStoreBrokerTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
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
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
