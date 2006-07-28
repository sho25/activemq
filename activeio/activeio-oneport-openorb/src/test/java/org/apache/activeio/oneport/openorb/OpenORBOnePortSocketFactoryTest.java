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
name|activeio
operator|.
name|oneport
operator|.
name|openorb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|RemoteException
import|;
end_import

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
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|oneport
operator|.
name|OnePortAsyncChannelServerTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|omg
operator|.
name|CORBA
operator|.
name|ORB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|omg
operator|.
name|CORBA
operator|.
name|Object
import|;
end_import

begin_import
import|import
name|org
operator|.
name|omg
operator|.
name|PortableServer
operator|.
name|POA
import|;
end_import

begin_import
import|import
name|org
operator|.
name|omg
operator|.
name|PortableServer
operator|.
name|POAHelper
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|BlockingQueue
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|OpenORBOnePortSocketFactoryTest
extends|extends
name|OnePortAsyncChannelServerTest
block|{
specifier|static
specifier|public
name|BlockingQueue
name|staticResultSlot
decl_stmt|;
specifier|private
name|ORB
name|orb
decl_stmt|;
specifier|private
name|String
name|serverRef
decl_stmt|;
specifier|private
name|TestIIOPServerImpl
name|testIIOPServer
decl_stmt|;
specifier|private
name|POA
name|rootPOA
decl_stmt|;
specifier|protected
name|void
name|startIIOPServer
parameter_list|()
throws|throws
name|Exception
block|{
name|staticResultSlot
operator|=
name|resultSlot
expr_stmt|;
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
literal|"org.omg.PortableInterceptor.ORBInitializerClass."
operator|+
name|OpenORBOpenPortFeatureInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"org.omg.CORBA.ORBClass"
argument_list|,
literal|"org.openorb.orb.core.ORB"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"org.omg.CORBA.ORBSingletonClass"
argument_list|,
literal|"org.openorb.orb.core.ORBSingleton"
argument_list|)
expr_stmt|;
name|OpenORBOpenPortFeatureInitializer
operator|.
name|setContextSocketFactory
argument_list|(
operator|new
name|OpenORBOpenPortSocketFactory
argument_list|(
name|server
argument_list|)
argument_list|)
expr_stmt|;
name|orb
operator|=
name|ORB
operator|.
name|init
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|,
name|props
argument_list|)
expr_stmt|;
name|OpenORBOpenPortFeatureInitializer
operator|.
name|setContextSocketFactory
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|rootPOA
operator|=
name|POAHelper
operator|.
name|narrow
argument_list|(
name|orb
operator|.
name|resolve_initial_references
argument_list|(
literal|"RootPOA"
argument_list|)
argument_list|)
expr_stmt|;
name|TestIIOPServerImpl
name|srv
init|=
operator|new
name|TestIIOPServerImpl
argument_list|()
decl_stmt|;
name|serverRef
operator|=
name|orb
operator|.
name|object_to_string
argument_list|(
name|srv
operator|.
name|_this
argument_list|(
name|orb
argument_list|)
argument_list|)
expr_stmt|;
name|rootPOA
operator|.
name|the_POAManager
argument_list|()
operator|.
name|activate
argument_list|()
expr_stmt|;
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|orb
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|stopIIOPServer
parameter_list|()
throws|throws
name|Exception
block|{
name|orb
operator|.
name|shutdown
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|hitIIOPServer
parameter_list|( )
throws|throws
name|NamingException
throws|,
name|RemoteException
block|{
comment|// Create a client side orb.
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
literal|"org.omg.CORBA.ORBClass"
argument_list|,
literal|"org.openorb.orb.core.ORB"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"org.omg.CORBA.ORBSingletonClass"
argument_list|,
literal|"org.openorb.orb.core.ORBSingleton"
argument_list|)
expr_stmt|;
name|ORB
name|orb
init|=
name|ORB
operator|.
name|init
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|Object
name|obj
init|=
name|orb
operator|.
name|string_to_object
argument_list|(
name|serverRef
argument_list|)
decl_stmt|;
name|TestIIOPServer
name|srv
init|=
name|TestIIOPServerHelper
operator|.
name|narrow
argument_list|(
name|obj
argument_list|)
decl_stmt|;
try|try
block|{
name|srv
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|orb
operator|.
name|shutdown
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

