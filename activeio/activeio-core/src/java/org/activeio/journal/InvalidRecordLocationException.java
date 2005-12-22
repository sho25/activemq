begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|journal
package|;
end_package

begin_comment
comment|/**  * Exception thrown by a Journal to indicate that an invalid RecordLocation was detected.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|InvalidRecordLocationException
extends|extends
name|Exception
block|{
comment|/**      * Comment for<code>serialVersionUID</code>      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|3618414947307239475L
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|InvalidRecordLocationException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @param msg 	 */
specifier|public
name|InvalidRecordLocationException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param msg 	 * @param rootCause 	 */
specifier|public
name|InvalidRecordLocationException
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|rootCause
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|,
name|rootCause
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param rootCause 	 */
specifier|public
name|InvalidRecordLocationException
parameter_list|(
name|Throwable
name|rootCause
parameter_list|)
block|{
name|super
argument_list|(
name|rootCause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

