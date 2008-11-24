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
name|kahadb
operator|.
name|replication
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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

begin_comment
comment|/**  * This broker service actually does not do anything.  It allows you to create an activemq.xml file  * which does not actually start a broker.  Used in conjunction with the ReplicationService since  * he will create the actual BrokerService  *   * @author chirino  * @org.apache.xbean.XBean element="kahadbReplicationBroker"  */
end_comment

begin_class
specifier|public
class|class
name|ReplicationBrokerService
extends|extends
name|BrokerService
block|{
name|ReplicationService
name|replicationService
decl_stmt|;
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|public
name|ReplicationService
name|getReplicationService
parameter_list|()
block|{
return|return
name|replicationService
return|;
block|}
specifier|public
name|void
name|setReplicationService
parameter_list|(
name|ReplicationService
name|replicationService
parameter_list|)
block|{
name|this
operator|.
name|replicationService
operator|=
name|replicationService
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|replicationService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|replicationService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

