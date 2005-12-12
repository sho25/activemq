begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|com
operator|.
name|panacya
operator|.
name|platform
operator|.
name|service
operator|.
name|bus
operator|.
name|sender
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|EJBObject
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|RemoteException
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:michael.gaffney@panacya.com">Michael Gaffney</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Sender
extends|extends
name|EJBObject
block|{
specifier|public
name|void
name|sendMessage
parameter_list|(
name|String
name|message
parameter_list|)
throws|throws
name|RemoteException
throws|,
name|SenderException
function_decl|;
block|}
end_interface

end_unit

