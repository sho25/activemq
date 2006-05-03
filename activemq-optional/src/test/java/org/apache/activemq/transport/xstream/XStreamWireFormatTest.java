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
name|xstream
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|command
operator|.
name|WireFormat
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
name|Command
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
name|MessageTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|XStreamWireFormatTest
extends|extends
name|MessageTest
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|XStreamWireFormatTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|assertBeanMarshalls
parameter_list|(
name|Object
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|assertBeanMarshalls
argument_list|(
name|original
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
name|getXStreamWireFormat
argument_list|()
operator|.
name|toString
argument_list|(
operator|(
name|Command
operator|)
name|original
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
name|original
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" as XML is:"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|xml
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|XStreamWireFormat
name|getXStreamWireFormat
parameter_list|()
block|{
return|return
operator|(
name|XStreamWireFormat
operator|)
name|wireFormat
return|;
block|}
specifier|protected
name|WireFormat
name|createWireFormat
parameter_list|()
block|{
return|return
operator|new
name|XStreamWireFormat
argument_list|()
return|;
block|}
block|}
end_class

end_unit

