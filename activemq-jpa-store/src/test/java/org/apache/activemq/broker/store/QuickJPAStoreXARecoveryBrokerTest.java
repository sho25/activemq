begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|store
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|XARecoveryBrokerTest
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
name|store
operator|.
name|jpa
operator|.
name|JPAReferenceStoreAdapter
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
name|store
operator|.
name|quick
operator|.
name|QuickPersistenceAdapter
import|;
end_import

begin_comment
comment|/**  * Used to verify that recovery works correctly against   *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|QuickJPAStoreXARecoveryBrokerTest
extends|extends
name|XARecoveryBrokerTest
block|{
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|QuickJPAStoreXARecoveryBrokerTest
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
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|service
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|QuickPersistenceAdapter
name|pa
init|=
operator|new
name|QuickPersistenceAdapter
argument_list|()
decl_stmt|;
name|JPAReferenceStoreAdapter
name|rfa
init|=
operator|new
name|JPAReferenceStoreAdapter
argument_list|()
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"openjpa.ConnectionDriverName"
argument_list|,
literal|"org.apache.derby.jdbc.EmbeddedDriver"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"openjpa.ConnectionURL"
argument_list|,
literal|"jdbc:derby:activemq-data/derby;create=true"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"openjpa.jdbc.SynchronizeMappings"
argument_list|,
literal|"buildSchema"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"openjpa.Log"
argument_list|,
literal|"DefaultLevel=WARN,SQL=TRACE"
argument_list|)
expr_stmt|;
name|rfa
operator|.
name|setEntityManagerProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|pa
operator|.
name|setReferenceStoreAdapter
argument_list|(
name|rfa
argument_list|)
expr_stmt|;
name|service
operator|.
name|setPersistenceAdapter
argument_list|(
name|pa
argument_list|)
expr_stmt|;
return|return
name|service
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
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|QuickPersistenceAdapter
name|pa
init|=
operator|new
name|QuickPersistenceAdapter
argument_list|()
decl_stmt|;
name|JPAReferenceStoreAdapter
name|rfa
init|=
operator|new
name|JPAReferenceStoreAdapter
argument_list|()
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"openjpa.ConnectionDriverName"
argument_list|,
literal|"org.apache.derby.jdbc.EmbeddedDriver"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"openjpa.ConnectionURL"
argument_list|,
literal|"jdbc:derby:activemq-data/derby;create=true"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"openjpa.jdbc.SynchronizeMappings"
argument_list|,
literal|"buildSchema"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"openjpa.Log"
argument_list|,
literal|"DefaultLevel=WARN,SQL=TRACE"
argument_list|)
expr_stmt|;
name|rfa
operator|.
name|setEntityManagerProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|pa
operator|.
name|setReferenceStoreAdapter
argument_list|(
name|rfa
argument_list|)
expr_stmt|;
name|service
operator|.
name|setPersistenceAdapter
argument_list|(
name|pa
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
block|}
end_class

end_unit

