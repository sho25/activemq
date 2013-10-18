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
name|jms
operator|.
name|pool
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
name|TimeUnit
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
name|ActiveMQConnectionFactory
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
name|TransportConnector
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
name|test
operator|.
name|TestSupport
import|;
end_import

begin_class
specifier|public
class|class
name|ConnectionExpiryEvictsFromPoolTest
extends|extends
name|TestSupport
block|{
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
specifier|private
name|PooledConnectionFactory
name|pooledFactory
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"mock:"
operator|+
name|connector
operator|.
name|getConnectUri
argument_list|()
argument_list|)
expr_stmt|;
name|pooledFactory
operator|=
operator|new
name|PooledConnectionFactory
argument_list|()
expr_stmt|;
name|pooledFactory
operator|.
name|setConnectionFactory
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|pooledFactory
operator|.
name|setMaxConnections
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEvictionOfIdle
parameter_list|()
throws|throws
name|Exception
block|{
name|pooledFactory
operator|.
name|setIdleTimeout
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|PooledConnection
name|connection
init|=
operator|(
name|PooledConnection
operator|)
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Connection
name|amq1
init|=
name|connection
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// let it idle timeout
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|PooledConnection
name|connection2
init|=
operator|(
name|PooledConnection
operator|)
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Connection
name|amq2
init|=
name|connection2
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"not equal"
argument_list|,
operator|!
name|amq1
operator|.
name|equals
argument_list|(
name|amq2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEvictionOfExpired
parameter_list|()
throws|throws
name|Exception
block|{
name|pooledFactory
operator|.
name|setExpiryTimeout
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Connection
name|amq1
init|=
operator|(
operator|(
name|PooledConnection
operator|)
name|connection
operator|)
operator|.
name|getConnection
argument_list|()
decl_stmt|;
comment|// let it expire while in use
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|Connection
name|connection2
init|=
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Connection
name|amq2
init|=
operator|(
operator|(
name|PooledConnection
operator|)
name|connection2
operator|)
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"not equal"
argument_list|,
operator|!
name|amq1
operator|.
name|equals
argument_list|(
name|amq2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNotIdledWhenInUse
parameter_list|()
throws|throws
name|Exception
block|{
name|pooledFactory
operator|.
name|setIdleTimeout
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|PooledConnection
name|connection
init|=
operator|(
name|PooledConnection
operator|)
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|s
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
comment|// let connection to get idle
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// get a connection from pool again, it should be the same underlying connection
comment|// as before and should not be idled out since an open session exists.
name|PooledConnection
name|connection2
init|=
operator|(
name|PooledConnection
operator|)
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|connection
operator|.
name|getConnection
argument_list|()
argument_list|,
name|connection2
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
comment|// now the session is closed even when it should not be
try|try
block|{
comment|// any operation on session first checks whether session is closed
name|s
operator|.
name|getTransacted
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Session should be fine, instead: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|Connection
name|original
init|=
name|connection
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection2
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// let connection to get idle
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// get a connection from pool again, it should be a new Connection instance as the
comment|// old one should have been inactive and idled out.
name|PooledConnection
name|connection3
init|=
operator|(
name|PooledConnection
operator|)
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|original
argument_list|,
name|connection3
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
