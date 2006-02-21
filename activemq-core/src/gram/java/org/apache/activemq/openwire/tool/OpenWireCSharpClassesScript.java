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
name|openwire
operator|.
name|tool
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

begin_comment
comment|/**  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|OpenWireCSharpClassesScript
extends|extends
name|OpenWireClassesScript
block|{
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|filePostFix
operator|=
literal|".cs"
expr_stmt|;
name|destDir
operator|=
operator|new
name|File
argument_list|(
literal|"../openwire-dotnet/src/OpenWire.Client/Commands"
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|run
argument_list|()
return|;
block|}
block|}
end_class

end_unit

