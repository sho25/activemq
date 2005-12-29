begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|discovery
operator|.
name|simple
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|DiscoveryEvent
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
name|transport
operator|.
name|discovery
operator|.
name|DiscoveryAgent
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
name|transport
operator|.
name|discovery
operator|.
name|DiscoveryListener
import|;
end_import

begin_comment
comment|/**  * A simple DiscoveryAgent that allows static configuration of the discovered services.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|SimpleDiscoveryAgent
implements|implements
name|DiscoveryAgent
block|{
specifier|private
name|DiscoveryListener
name|listener
decl_stmt|;
name|String
name|services
index|[]
init|=
operator|new
name|String
index|[]
block|{}
decl_stmt|;
name|String
name|group
init|=
literal|"DEFAULT"
decl_stmt|;
specifier|public
name|void
name|setDiscoveryListener
parameter_list|(
name|DiscoveryListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
specifier|public
name|void
name|registerService
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|services
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|listener
operator|.
name|onServiceAdd
argument_list|(
operator|new
name|DiscoveryEvent
argument_list|(
name|services
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|String
index|[]
name|getServices
parameter_list|()
block|{
return|return
name|services
return|;
block|}
specifier|public
name|void
name|setServices
parameter_list|(
name|String
name|services
parameter_list|)
block|{
name|this
operator|.
name|services
operator|=
name|services
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setServices
parameter_list|(
name|String
name|services
index|[]
parameter_list|)
block|{
name|this
operator|.
name|services
operator|=
name|services
expr_stmt|;
block|}
specifier|public
name|void
name|setServices
parameter_list|(
name|URI
name|services
index|[]
parameter_list|)
block|{
name|this
operator|.
name|services
operator|=
operator|new
name|String
index|[
name|services
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|services
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|services
index|[
name|i
index|]
operator|=
name|services
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
specifier|public
name|void
name|setGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
block|}
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{     }
specifier|public
name|void
name|serviceFailed
parameter_list|(
name|DiscoveryEvent
name|event
parameter_list|)
throws|throws
name|IOException
block|{     }
block|}
end_class

end_unit

