begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (c) 2005 Your Corporation. All Rights Reserved.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|stomp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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

begin_interface
interface|interface
name|ResponseListener
block|{
comment|/**      * Return true if you handled this, false otherwise      */
name|boolean
name|onResponse
parameter_list|(
name|Response
name|receipt
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

